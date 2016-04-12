package org.telegram.bot.kernel;

import org.telegram.api.TLConfig;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.auth.TLRequestAuthLogOut;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.bot.BotConstants;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;

import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Ruben Bermudez
 * @author Hendrik Hofstadt
 * @version 2.0
 * @brief Authentication Service
 * @date 13.03.14
 */
public class KernelAuth {
    private static final String LOGTAG = "KERNELAUTH";
    private static final int TIMEUNTILCALLINGUSER = 60000;
    private static final int ERROR303 = 303;

    private final AbsApiState apiState;
    private final IKernelComm kernelComm;
    private final BotConfig config;
    private Timer loginTimer = new Timer();
    private Scanner scanner = new Scanner(System.in);


    public KernelAuth(AbsApiState apiState, BotConfig config, IKernelComm kernelComm) {
        this.kernelComm = kernelComm;
        this.apiState = apiState;
        this.config = config;
    }

    public boolean init() {
        return true;
    }

    public int start() {
        return login();
    }

    private Integer login() {
        int result = -1;
        try {
            if (getApiState().isAuthenticated()) {
                BotLogger.info(LOGTAG,"Found Auth file");
                config.isRegistered = true;
                result = -1;
            } else {
                try {
                    final TLConfig config = kernelComm.getApi().doRpcCallNonAuth(new TLRequestHelpGetConfig());
                    BotLogger.info(LOGTAG,"Loaded DC list");
                    getApiState().updateSettings(config);
                } catch (IOException | TimeoutException e) {
                    BotLogger.error(LOGTAG, e);
                }
                BotLogger.info(LOGTAG,"Sending code to phone " + config.number + "...");
                TLSentCode sentCode = null;
                try {
                    final TLRequestAuthSendCode tlRequestAuthSendCode = getSendCodeRequest();
                    sentCode = kernelComm.getApi().doRpcCallNonAuth(tlRequestAuthSendCode);
                    createNextCodeTimer(sentCode.getTimeout());
                } catch (RpcException e) {
                    if (e.getErrorCode() == ERROR303) {
                        final int destDC = updateDCWhenLogin(e);
                        if (destDC != -1) {
                            getApiState().setPrimaryDc(destDC);
                            kernelComm.getApi().switchToDc(destDC);
                            sentCode = retryLogin(destDC);
                        }
                    }
                } catch (TimeoutException e) {
                    BotLogger.error(LOGTAG, e);
                    sentCode = null;
                }

                if (sentCode != null) {
                    config.hashCode = sentCode.getPhoneCodeHash();
                    config.isRegistered = sentCode.isPhoneRegistered();
                    BotLogger.info(LOGTAG,"sent Code via " + sentCode.getType().toString());
                    do {
                        final String code = readCode();
                        if (!code.isEmpty() && setAuthCode(code)) {
                            result = 0;
                        }
                        if (result != 0) {
                            System.out.println("Incorrect code!");
                        }
                    } while (result != 0);
                } else {
                    result = -2;
                }
            }
        } catch (IOException | TimeoutException ex) {
            BotLogger.error(LOGTAG, ex);
            result = -3;
        }

        return result;
    }

    private String readCode() {
        String code = "";
        while (code.isEmpty()) {
            try {
                System.out.println("Insert login code:");
                code = scanner.next();
                Integer.parseInt(code);
            } catch (NumberFormatException e) {
                code = "";
            }
        }
        return code;
    }

    private void createNextCodeTimer(int timeout) {
        this.loginTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final TLRequestAuthSendCode tlRequestAuthSendCode = getSendCodeRequest();
                    TLSentCode sentCode = kernelComm.getApi().doRpcCallNonAuth(tlRequestAuthSendCode);
                    this.cancel();
                    createNextCodeTimer(sentCode.getTimeout());
                } catch (Exception e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }, (timeout == 0) ? TIMEUNTILCALLINGUSER : timeout);
    }

    private TLRequestAuthSendCode getSendCodeRequest() {
        final TLRequestAuthSendCode tlRequestAuthSendCode = new TLRequestAuthSendCode();
        tlRequestAuthSendCode.setPhoneNumber(config.number);
        tlRequestAuthSendCode.setApiId(BotConstants.API_KEY);
        tlRequestAuthSendCode.setApiHash(BotConstants.API_HASH);
        tlRequestAuthSendCode.setLangCode(config.languageCode);
        return tlRequestAuthSendCode;
    }

    private int updateDCWhenLogin(RpcException e) {
        final int destDC;
        if (e.getErrorTag().startsWith("NETWORK_MIGRATE_")) {
            destDC = Integer.parseInt(e.getErrorTag().substring("NETWORK_MIGRATE_".length()));
        } else if (e.getErrorTag().startsWith("PHONE_MIGRATE_")) {
            destDC = Integer.parseInt(e.getErrorTag().substring("PHONE_MIGRATE_".length()));
        } else if (e.getErrorTag().startsWith("USER_MIGRATE_")) {
            destDC = Integer.parseInt(e.getErrorTag().substring("USER_MIGRATE_".length()));
        } else {
            BotLogger.error(LOGTAG, e);
            destDC = -1;
        }
        return destDC;
    }

    private TLSentCode retryLogin(int destDC) throws IOException, TimeoutException {
        final TLSentCode sentCode;
        kernelComm.getApi().switchToDc(destDC);
        final TLRequestAuthSendCode tlRequestAuthSendCode = getSendCodeRequest();
        sentCode = kernelComm.getApi().doRpcCallNonAuth(tlRequestAuthSendCode);
        resetTimer();
        createNextCodeTimer(sentCode.getTimeout());
        return sentCode;
    }

    private void resetTimer() {
        this.loginTimer.cancel();
        this.loginTimer = new Timer();
    }

    public boolean setAuthCode(String code) {
        boolean result;
        try {
            if (config.hashCode.compareTo("") == 0) {
                result = false;
            } else {
                final TLAuthorization authorization;
                if (config.isRegistered) {
                    final TLRequestAuthSignIn tlRequestAuthSignIn = getSignInRequest(code);
                    authorization = kernelComm.getApi().doRpcCallNonAuth(tlRequestAuthSignIn);
                } else {
                    final TLRequestAuthSignUp tlRequestAuthSignUp = getSignUpRequest(code);
                    authorization = kernelComm.getApi().doRpcCallNonAuth(tlRequestAuthSignUp);
                }
                if (authorization != null) {
                    config.isRegistered = true;
                }

                getApiState().doAuth(authorization);
                BotLogger.info(LOGTAG,"Activation complete as #" + getApiState().getObj().getUid());
                kernelComm.getApi().doRpcCall(new TLRequestUpdatesGetState());
                BotLogger.info(LOGTAG,"Loaded initial state");
                resetTimer();
                result = true;
            }
        } catch (IOException | TimeoutException e) {
            BotLogger.error(LOGTAG, e);
            result = false;
        }

        return result;
    }

    private TLRequestAuthSignUp getSignUpRequest(String code) {
        final TLRequestAuthSignUp tlRequestAuthSignUp = new TLRequestAuthSignUp();
        tlRequestAuthSignUp.setPhoneNumber(config.number);
        tlRequestAuthSignUp.setPhoneCodeHash(config.hashCode);
        tlRequestAuthSignUp.setPhoneCode(code);
        tlRequestAuthSignUp.setFirstName("Rubenlagus");
        tlRequestAuthSignUp.setLastName("Bot");
        return tlRequestAuthSignUp;
    }

    private TLRequestAuthSignIn getSignInRequest(String code) {
        final TLRequestAuthSignIn tlRequestAuthSignIn = new TLRequestAuthSignIn();
        tlRequestAuthSignIn.setPhoneNumber(config.number);
        tlRequestAuthSignIn.setPhoneCodeHash(config.hashCode);
        tlRequestAuthSignIn.setPhoneCode(code);
        return tlRequestAuthSignIn;
    }

    /**
     * Logout from Telegram
     */
    public boolean logOut() {
        boolean result;
        try {
            if (getApiState().isAuthenticated()) {
                kernelComm.doRpcCallSync(new TLRequestAuthLogOut()); // Logout previous
                getApiState().resetAuth(); // Reset previous stored credentials
                config.isRegistered = false;
            } else {
                config.isRegistered = false;
            }
            result = true;
        } catch (ExecutionException e) {
            BotLogger.error(LOGTAG, e);
            result = false;
        }

        return result;
    }

    public MemoryApiState getApiState() {
        return (MemoryApiState) this.apiState;
    }

    public int getCurrentUserId() {
        return this.apiState.getUserId();
    }
}

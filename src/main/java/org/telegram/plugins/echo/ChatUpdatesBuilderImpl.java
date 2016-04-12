package org.telegram.plugins.echo;

import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.structure.BotConfig;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;
import org.telegram.plugins.echo.handlers.CustomUpdatesHandler;
import org.telegram.plugins.echo.handlers.interfaces.IKernelCommSetter;
import org.telegram.plugins.echo.handlers.interfaces.IMessageHandler;
import org.telegram.plugins.echo.handlers.interfaces.ITLMessageHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 01 of April of 2016
 */
@SuppressWarnings("ReturnOfThis")
public class ChatUpdatesBuilderImpl implements ChatUpdatesBuilder {
    private final Class<CustomUpdatesHandler> updatesHandlerBase;
    private final List<IKernelCommSetter> kernelCommNeeds = new ArrayList<>();
    private IKernelComm kernelComm;
    private IUsersHandler usersHandler;
    private BotConfig botConfig;
    private IChatsHandler chatsHandler;
    private IMessageHandler messageHandler;
    private ITLMessageHandler tlMessageHandler;
    private IDifferenceParametersService differenceParametersService;
    private DatabaseManager databaseManager;

    public ChatUpdatesBuilderImpl(Class<CustomUpdatesHandler> updatesHandlerBase) {
        this.updatesHandlerBase = updatesHandlerBase;
    }

    @Override
    public void setKernelComm(IKernelComm kernelComm) {
        this.kernelComm = kernelComm;
    }

    @Override
    public void setDifferenceParametersService(IDifferenceParametersService differenceParametersService) {
        this.differenceParametersService = differenceParametersService;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManagerImpl();
        }
        return databaseManager;
    }

    public void registerNewKernelCommSetter(IKernelCommSetter kernelCommSetter) {
        kernelCommNeeds.add(kernelCommSetter);
    }

    public ChatUpdatesBuilderImpl setUsersHandler(IUsersHandler usersHandler) {
        this.usersHandler = usersHandler;
        return this;
    }

    public ChatUpdatesBuilderImpl setChatsHandler(IChatsHandler chatsHandler) {
        this.chatsHandler = chatsHandler;
        return this;
    }

    public ChatUpdatesBuilderImpl setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
        return this;
    }

    public ChatUpdatesBuilderImpl setMessageHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public ChatUpdatesBuilderImpl setTlMessageHandler(ITLMessageHandler tlMessageHandler) {
        this.tlMessageHandler = tlMessageHandler;
        return this;
    }

    @Override
    public UpdatesHandlerBase build() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (kernelComm == null) {
            throw new NullPointerException("Can't build the handler without a KernelComm");
        }
        if (differenceParametersService == null) {
            throw new NullPointerException("Can't build the handler without a differenceParamtersService");
        }

        setKernelCommToHandlers();
        final Constructor<CustomUpdatesHandler> constructor = updatesHandlerBase.getConstructor(IKernelComm.class,
                IDifferenceParametersService.class, DatabaseManager.class);
        final CustomUpdatesHandler updatesHandler =
                constructor.newInstance(kernelComm, differenceParametersService, getDatabaseManager());
        updatesHandler.setConfig(botConfig);
        updatesHandler.setHandlers(messageHandler, usersHandler, chatsHandler, tlMessageHandler);
        return updatesHandler;
    }

    private void setKernelCommToHandlers() {
        kernelCommNeeds.forEach(x -> x.setKernelComm(kernelComm));
    }
}

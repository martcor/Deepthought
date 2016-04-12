package org.telegram.plugins.echo.handlers;

import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.echo.handlers.interfaces.IMessageHandler;
import org.telegram.plugins.echo.handlers.interfaces.ITLMessageHandler;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Handler for TLAbsMessages
 * @date 26 of May of 2015
 */
public class TLMessageHandler implements ITLMessageHandler {
    private static final String LOGTAG = "TLMESSAGEHANDLER";
    private final IMessageHandler messageHandler;
    private final DatabaseManager databaseManager;

    public TLMessageHandler(IMessageHandler messageHandler, DatabaseManager databaseManager) {
        this.messageHandler = messageHandler;
        this.databaseManager = databaseManager;
    }

    @Override
    public void onTLMessage(TLMessage message) {
        final TLAbsPeer absPeer = message.getToId();
        if (absPeer instanceof TLPeerUser) {
            onTLMessageForUser(message);
        } else {
            BotLogger.severe(LOGTAG, "Unsupported Peer: " + absPeer.toString());
        }
    }

    private void onTLMessageForUser(TLMessage message) {
        if (!message.isSent()) {
            final IUser user = databaseManager.getUserById(message.getFromId());
            if (user != null) {
                this.messageHandler.handleMessage(user, message);
            }
        }
    }
}

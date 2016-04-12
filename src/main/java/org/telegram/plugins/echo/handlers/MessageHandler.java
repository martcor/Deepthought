package org.telegram.plugins.echo.handlers;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.echo.handlers.interfaces.IMessageHandler;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Manager for a private messages
 * @date 21/11/14
 */
public class MessageHandler implements IMessageHandler {
    private IKernelComm kernelComm;

    public MessageHandler() {
    }

    @Override
    public void setKernelComm(IKernelComm kernelComm) {
        this.kernelComm = kernelComm;
    }

    /**
     * Handler for the request of a contact
     *
     * @param user    User to be answered
     * @param message TLMessage received
     */
    @Override
    public void handleMessage(@NotNull IUser user, @NotNull TLMessage message) {
        handleMessageInternal(user, message.getMessage());
    }

    /**
     * Handler for the requests of a contact
     *
     * @param user    User to be answered
     * @param message Message received
     */
    @Override
    public void handleMessage(@NotNull IUser user, @NotNull TLUpdateShortMessage message) {
        handleMessageInternal(user, message.getMessage());
    }

    /**
     * Handle a message from an user
     * @param user User that sent the message
     * @param message Message received
     */
    private void handleMessageInternal(@NotNull IUser user, String message) {
        kernelComm.sendMessage(user, message);
        kernelComm.performMarkAsRead(user, 0);
    }
}
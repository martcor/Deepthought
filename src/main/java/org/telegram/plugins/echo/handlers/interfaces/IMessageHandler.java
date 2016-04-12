package org.telegram.plugins.echo.handlers.interfaces;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.structure.IUser;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Interface for messages
 * @date 23 of April of 2015
 */
public interface IMessageHandler extends IKernelCommSetter {
    void handleMessage(@NotNull IUser user, @NotNull TLMessage message);

    void handleMessage(@NotNull IUser user, @NotNull TLUpdateShortMessage message);
}

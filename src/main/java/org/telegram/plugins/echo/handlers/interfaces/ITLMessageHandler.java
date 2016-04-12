package org.telegram.plugins.echo.handlers.interfaces;

import org.telegram.api.message.TLMessage;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Handler for telegram messages
 * @date 26 of May of 2015
 */
public interface ITLMessageHandler {
    void onTLMessage(TLMessage message);
}

package org.telegram.bot.factories;

import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.input.peer.TLInputPeerChannel;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.input.peer.TLInputPeerUser;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Factory for TLAbsInputUser
 * @date 27 of May of 2015
 */
public class TLFactory {
    public static TLAbsInputPeer createTLInputPeer(IUser user, Chat chat) {
        final TLAbsInputPeer tlInputPeer;
        if (user == null) {
            if (chat == null) {
                tlInputPeer = new TLInputPeerSelf();
            } else {
                if (chat != null) {
                    if (chat.isChannel()) {
                        final TLInputPeerChannel inputPeerChannel = new TLInputPeerChannel();
                        inputPeerChannel.setChannelId(chat.getId());
                        inputPeerChannel.setAccessHash(chat.getAccessHash());
                        tlInputPeer = inputPeerChannel;
                    } else {
                        final TLInputPeerChat tlInputPeerChat = new TLInputPeerChat();
                        tlInputPeerChat.setChatId(chat.getId());
                        tlInputPeer = tlInputPeerChat;
                    }
                } else {
                    tlInputPeer = null;
                }
            }
        } else {
            final TLInputPeerUser tlInputPeerUser = new TLInputPeerUser();
            tlInputPeerUser.setUserId(user.getUserId());
            tlInputPeerUser.setAccessHash(user.getUserHash());
            tlInputPeer = tlInputPeerUser;
        }

        return tlInputPeer;
    }
}

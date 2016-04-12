package org.telegram.plugins.echo.handlers;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.TLChatForbidden;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelForbidden;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.Chat;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;

import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 01 of April of 2016
 */
public class ChatsHandler implements IChatsHandler {
    private static final String LOGTAG = "CHATSHANDLER";
    private final DatabaseManagerImpl databaseManager;

    public ChatsHandler(DatabaseManagerImpl databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onChats(List<TLAbsChat> chats) {
        chats.stream().forEach(this::onAbsChat);
    }

    private void onAbsChat(TLAbsChat chat) {
        if (chat instanceof TLChannel) {
            onChannel((TLChannel) chat);
        } else if (chat instanceof TLChannelForbidden) {
            onChannelForbidden((TLChannelForbidden) chat);
        } else if (chat instanceof TLChat) {
            onChat((TLChat) chat);
        } else if (chat instanceof TLChatForbidden) {
            onChatForbidden((TLChatForbidden) chat);
        } else {
            BotLogger.warn(LOGTAG, "Unsupported chat type " + chat);
        }
    }

    private void onChatForbidden(TLChatForbidden chat) {
        boolean updating = true;
        Chat current = databaseManager.getChatById(chat.getId());
        if (current == null) {
            updating = false;
            current = new Chat(chat.getId());
        }

        current.setFlags(0);
        current.setTitle(chat.getTitle());
        current.setForbidden(true);
        current.setChannel(false);
        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }

    private void onChat(TLChat chat) {
        boolean updating = true;
        Chat current = databaseManager.getChatById(chat.getId());
        if (current == null) {
            updating = false;
            current = new Chat(chat.getId());
        }

        current.setChannel(false);
        current.setFlags(chat.getFlags());
        current.setTitle(chat.getTitle());
        current.setForbidden(chat.isForbidden());

        if (chat.isMigratedTo()) {
            current.setMigratedTo(chat.getMigratedTo().getChannelId());
        }

        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }

    private void onChannelForbidden(TLChannelForbidden channel) {
        boolean updating = true;
        Chat current = databaseManager.getChatById(channel.getId());
        if (current == null) {
            updating = false;
            current = new Chat(channel.getId());
        }

        current.setChannel(true);
        current.setAccessHash(channel.getAccessHash());
        current.setFlags(0);
        current.setTitle(channel.getTitle());
        current.setForbidden(true);

        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }

    private void onChannel(TLChannel channel) {
        boolean updating = true;
        Chat current = databaseManager.getChatById(channel.getId());
        if (current == null) {
            updating = false;
            current = new Chat(channel.getId());
        }

        current.setChannel(true);
        if (channel.hasAccessHash()) {
            current.setAccessHash(channel.getAccessHash());
        }
        current.setFlags(channel.getFlags());
        current.setTitle(channel.getTitle());
        current.setForbidden(false);
        if (channel.hasUsername()) {
            current.setUsername(channel.getUsername());
        }
        if (channel.hasRestrictionReason()) {
            current.setRestrictionReason(channel.getRestrictionReason());
        }

        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }
}

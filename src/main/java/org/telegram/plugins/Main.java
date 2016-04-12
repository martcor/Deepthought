package org.telegram.plugins;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.plugins.echo.ChatUpdatesBuilderImpl;
import org.telegram.plugins.echo.database.DatabaseManagerImpl;
import org.telegram.plugins.echo.handlers.ChatsHandler;
import org.telegram.plugins.echo.handlers.CustomUpdatesHandler;
import org.telegram.plugins.echo.handlers.MessageHandler;
import org.telegram.plugins.echo.handlers.TLMessageHandler;
import org.telegram.plugins.echo.handlers.UsersHandler;
import org.telegram.plugins.echo.handlers.interfaces.IMessageHandler;
import org.telegram.plugins.echo.handlers.interfaces.ITLMessageHandler;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Hendrik Hofstadt on 13.03.14.
 */
public class Main {
    public static void main(String[] args) {
        Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
        Logger.getRootLogger().setLevel(Level.ALL);

        final DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();

        final BotConfig botConfig = new BotConfig();
        botConfig.number = "<your-own-phone-number>";
        botConfig.authfile = botConfig.number + ".auth";

        final IUsersHandler usersHandler = new UsersHandler(databaseManager);
        final IChatsHandler chatsHandler = new ChatsHandler(databaseManager);
        final IMessageHandler messageHandler = new MessageHandler();
        final ITLMessageHandler tlMessageHandler = new TLMessageHandler(messageHandler, databaseManager);

        final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(CustomUpdatesHandler.class);
        builder.setBotConfig(botConfig)
                .setUsersHandler(usersHandler)
                .setChatsHandler(chatsHandler)
                .setMessageHandler(messageHandler)
                .setTlMessageHandler(tlMessageHandler);
        builder.registerNewKernelCommSetter(messageHandler);

        try {
            final TelegramBot kernel = new TelegramBot(botConfig, builder);
            kernel.init();
            kernel.startBot();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            BotLogger.severe("MAIN", e);
        }
    }
}
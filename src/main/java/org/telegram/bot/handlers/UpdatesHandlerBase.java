package org.telegram.bot.handlers;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.TLMessageService;
import org.telegram.api.notify.peer.TLAbsNotifyPeer;
import org.telegram.api.notify.peer.TLNotifyPeer;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.update.TLFakeUpdate;
import org.telegram.api.update.TLUpdateBotCallbackQuery;
import org.telegram.api.update.TLUpdateBotInlineQuery;
import org.telegram.api.update.TLUpdateBotInlineSend;
import org.telegram.api.update.TLUpdateChannel;
import org.telegram.api.update.TLUpdateChannelGroup;
import org.telegram.api.update.TLUpdateChannelMessageViews;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateChannelPinnedMessage;
import org.telegram.api.update.TLUpdateChannelTooLong;
import org.telegram.api.update.TLUpdateChatAdmin;
import org.telegram.api.update.TLUpdateChatParticipantAdd;
import org.telegram.api.update.TLUpdateChatParticipantAdmin;
import org.telegram.api.update.TLUpdateChatParticipantDelete;
import org.telegram.api.update.TLUpdateChatParticipants;
import org.telegram.api.update.TLUpdateChatUserTyping;
import org.telegram.api.update.TLUpdateContactLink;
import org.telegram.api.update.TLUpdateContactRegistered;
import org.telegram.api.update.TLUpdateDcOptions;
import org.telegram.api.update.TLUpdateDeleteChannelMessages;
import org.telegram.api.update.TLUpdateDeleteMessages;
import org.telegram.api.update.TLUpdateEditChannelMessage;
import org.telegram.api.update.TLUpdateEditMessage;
import org.telegram.api.update.TLUpdateInlineBotCallbackQuery;
import org.telegram.api.update.TLUpdateMessageId;
import org.telegram.api.update.TLUpdateNewAuthorization;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.update.TLUpdateNewStickerSet;
import org.telegram.api.update.TLUpdateNotifySettings;
import org.telegram.api.update.TLUpdatePrivacy;
import org.telegram.api.update.TLUpdateReadChannelInbox;
import org.telegram.api.update.TLUpdateReadMessagesContents;
import org.telegram.api.update.TLUpdateReadMessagesInbox;
import org.telegram.api.update.TLUpdateReadMessagesOutbox;
import org.telegram.api.update.TLUpdateSavedGifs;
import org.telegram.api.update.TLUpdateServiceNotification;
import org.telegram.api.update.TLUpdateStickerSets;
import org.telegram.api.update.TLUpdateStickerSetsOrder;
import org.telegram.api.update.TLUpdateUserBlocked;
import org.telegram.api.update.TLUpdateUserName;
import org.telegram.api.update.TLUpdateUserPhone;
import org.telegram.api.update.TLUpdateUserPhoto;
import org.telegram.api.update.TLUpdateUserStatus;
import org.telegram.api.update.TLUpdateUserTyping;
import org.telegram.api.update.TLUpdateWebPage;
import org.telegram.api.updates.TLUpdateShortChatMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.updates.TLUpdateShortSentMessage;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.difference.TLAbsDifference;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.interfaces.IDifferencesHandler;
import org.telegram.bot.handlers.interfaces.IUpdatesHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.UpdateWrapper;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.Chat;
import org.telegram.tl.TLObject;

import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Base plugins of the updates handler, should be provided to MainHandler.
 * Has some final method, but also provide option to add custom behaviour
 * @date 22 of March of 2016
 */
@SuppressWarnings("OverlyComplexClass")
public abstract class UpdatesHandlerBase implements IUpdatesHandler {
    private static final String LOGTAG = "UPDATESHANDLERBASE";

    private final IDifferenceParametersService differenceParametersService;
    private final IDifferencesHandler differencesHandler;
    private final DatabaseManager databaseManager;

    UpdatesHandlerBase(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.differenceParametersService = differenceParametersService;
        differencesHandler = new DifferencesHandler(kernelComm, differenceParametersService, this);
    }

    @Override
    public final void processUpdate(UpdateWrapper updateWrapper) {
        boolean canHandle = true;
        if (updateWrapper.isCheckPts()) {
            canHandle = checkPts(updateWrapper);
        }

        if (canHandle) {
            final TLObject update = updateWrapper.getUpdate();
            if (update instanceof TLUpdateShortMessage) {
                onTLUpdateShortMessage((TLUpdateShortMessage) update);
            } else if (update instanceof TLUpdateShortChatMessage) {
                onTLUpdateShortChatMessage((TLUpdateShortChatMessage) update);
            } else if (update instanceof TLUpdateShortSentMessage) {
                onTLUpdateShortSentMessage((TLUpdateShortSentMessage) update);
            } else if (update instanceof TLUpdateNewMessage) {
                onTLUpdateNewMessage((TLUpdateNewMessage) update);
            } else if (update instanceof TLUpdateChatParticipants) {
                onTLUpdateChatParticipants((TLUpdateChatParticipants) update);
            } else if (update instanceof TLUpdateChannelNewMessage) {
                onTLUpdateChannelNewMessage((TLUpdateChannelNewMessage) update);
            } else if (update instanceof TLUpdateChannel) {
                onTLUpdateChannel((TLUpdateChannel) update);
            } else if (update instanceof TLUpdateBotInlineQuery) {
                onTLUpdateBotInlineQuery((TLUpdateBotInlineQuery) update);
            } else if (update instanceof TLUpdateBotInlineSend) {
                onTLUpdateBotInlineSend((TLUpdateBotInlineSend) update);
            } else if (update instanceof TLUpdateChannelGroup) {
                onTLUpdateChannelGroup((TLUpdateChannelGroup) update);
            } else if (update instanceof TLUpdateChannelMessageViews) {
                onTLUpdateChannelMessageViews((TLUpdateChannelMessageViews) update);
            } else if (update instanceof TLUpdateChannelPinnedMessage) {
                onTLUpdateChannelPinnedMessage((TLUpdateChannelPinnedMessage) update);
            } else if (update instanceof TLUpdateChannelTooLong) {
                onTLUpdateChannelTooLong((TLUpdateChannelTooLong) update);
            } else if (update instanceof TLUpdateChatAdmin) {
                onTLUpdateChatAdmin((TLUpdateChatAdmin) update);
            } else if (update instanceof TLUpdateChatParticipantAdd) {
                onTLUpdateChatParticipantAdd((TLUpdateChatParticipantAdd) update);
            } else if (update instanceof TLUpdateChatParticipantAdmin) {
                onTLUpdateChatParticipantAdmin((TLUpdateChatParticipantAdmin) update);
            } else if (update instanceof TLUpdateChatParticipantDelete) {
                onTLUpdateChatParticipantDelete((TLUpdateChatParticipantDelete) update);
            } else if (update instanceof TLUpdateChatUserTyping) {
                onTLUpdateChatUserTyping((TLUpdateChatUserTyping) update);
            } else if (update instanceof TLUpdateContactLink) {
                onTLUpdateContactLink((TLUpdateContactLink) update);
            } else if (update instanceof TLUpdateContactRegistered) {
                onTLUpdateContactRegistered((TLUpdateContactRegistered) update);
            } else if (update instanceof TLUpdateDcOptions) {
                onTLUpdateDcOptions((TLUpdateDcOptions) update);
            } else if (update instanceof TLUpdateDeleteChannelMessages) {
                onTLUpdateDeleteChannelMessages((TLUpdateDeleteChannelMessages) update);
            } else if (update instanceof TLUpdateDeleteMessages) {
                onTLUpdateDeleteMessages((TLUpdateDeleteMessages) update);
            } else if (update instanceof TLUpdateEditChannelMessage) {
                onTLUpdateEditChannelMessage((TLUpdateEditChannelMessage) update);
            } else if (update instanceof TLUpdateMessageId) {
                onTLUpdateMessageId((TLUpdateMessageId) update);
            } else if (update instanceof TLUpdateNewAuthorization) {
                onTLUpdateNewAuthorization((TLUpdateNewAuthorization) update);
            } else if (update instanceof TLUpdateNewStickerSet) {
                onTLUpdateNewStickerSet((TLUpdateNewStickerSet) update);
            } else if (update instanceof TLUpdateNotifySettings) {
                onTLUpdateNotifySettings((TLUpdateNotifySettings) update);
            } else if (update instanceof TLUpdatePrivacy) {
                onTLUpdatePrivacy((TLUpdatePrivacy) update);
            } else if (update instanceof TLUpdateReadChannelInbox) {
                onTLUpdateReadChannelInbox((TLUpdateReadChannelInbox) update);
            } else if (update instanceof TLUpdateReadMessagesContents) {
                onTLUpdateReadMessagesContents((TLUpdateReadMessagesContents) update);
            } else if (update instanceof TLUpdateReadMessagesInbox) {
                onTLUpdateReadMessagesInbox((TLUpdateReadMessagesInbox) update);
            } else if (update instanceof TLUpdateReadMessagesOutbox) {
                onTLUpdateReadMessagesOutbox((TLUpdateReadMessagesOutbox) update);
            } else if (update instanceof TLUpdateSavedGifs) {
                onTLUpdateSavedGifs((TLUpdateSavedGifs) update);
            } else if (update instanceof TLUpdateServiceNotification) {
                onTLUpdateServiceNotification((TLUpdateServiceNotification) update);
            } else if (update instanceof TLUpdateStickerSets) {
                onTLUpdateStickerSets((TLUpdateStickerSets) update);
            } else if (update instanceof TLUpdateStickerSetsOrder) {
                onTLUpdateStickerSetsOrder((TLUpdateStickerSetsOrder) update);
            } else if (update instanceof TLUpdateUserBlocked) {
                onTLUpdateUserBlocked((TLUpdateUserBlocked) update);
            } else if (update instanceof TLUpdateUserName) {
                onTLUpdateUserName((TLUpdateUserName) update);
            } else if (update instanceof TLUpdateUserPhone) {
                onTLUpdateUserPhone((TLUpdateUserPhone) update);
            } else if (update instanceof TLUpdateUserPhoto) {
                onTLUpdateUserPhoto((TLUpdateUserPhoto) update);
            } else if (update instanceof TLUpdateUserStatus) {
                onTLUpdateUserStatus((TLUpdateUserStatus) update);
            } else if (update instanceof TLUpdateUserTyping) {
                onTLUpdateUserTyping((TLUpdateUserTyping) update);
            } else if (update instanceof TLUpdateWebPage) {
                onTLUpdateWebPage((TLUpdateWebPage) update);
            } else if (update instanceof TLFakeUpdate) {
                onTLFakeUpdate((TLFakeUpdate) update);
            } else if (update instanceof TLUpdateBotCallbackQuery) {
                onTLUpdateBotCallbackQuery((TLUpdateBotCallbackQuery) update);
            } else if (update instanceof TLUpdateEditMessage) {
                onTLUpdateEditMessage((TLUpdateEditMessage) update);
            } else if (update instanceof TLUpdateInlineBotCallbackQuery) {
                onTLUpdateInlineBotCallbackQuery((TLUpdateInlineBotCallbackQuery) update);
            } else {
                BotLogger.debug(LOGTAG, "Unsupported TLAbsUpdate: " + update.toString());
            }
            if (updateWrapper.isUpdatePts()){
                updatePts(updateWrapper);
            }
        }
    }

    private boolean checkPts(UpdateWrapper updateWrapper) {
        final boolean canHandle;

        final int pts = differenceParametersService.getPts(updateWrapper.getChannelId());
        final int newPts = pts + updateWrapper.getPtsCount();

        if ((updateWrapper.getPts() == 0) || (newPts == updateWrapper.getPts())) {
            canHandle = true;
        } else {
            BotLogger.warn(LOGTAG, "Discarded " + updateWrapper.toString() + " with newPts: "
                    + newPts + "(" + pts +") and pts: " + updateWrapper.getPts());
            canHandle = false;
            if (newPts < updateWrapper.getPts()) {
                if (!updateWrapper.isChannel() || isChatMissing(updateWrapper.getChannelId())) {
                    getDifferences();
                } else {
                    final Chat chat = databaseManager.getChatById(updateWrapper.getChannelId());
                    if (chat != null) {
                        differencesHandler.getChannelDifferences(chat.getId(), chat.getAccessHash());
                    }
                }
            }
        }

        return canHandle;
    }

    @Override
    public final boolean checkSeq(int seq, int seqStart, int date) {
        boolean canHandle = false;

        seqStart = (seqStart == 0) ? seq : seqStart;
        if (seqStart == (differenceParametersService.getSeq(0) + 1)) {
            canHandle = true;
        }

        return canHandle;
    }

    @Override
    public final void getDifferences() {
        differencesHandler.getDifferences();
    }

    private void updatePts(UpdateWrapper updateWrapper) {
        differenceParametersService.setNewUpdateParams(updateWrapper.getChannelId(), updateWrapper.getPts(),
                updateWrapper.getSeq(), updateWrapper.getDate());
    }

    private void onTLUpdateShortMessage(TLUpdateShortMessage update) {
        if (isUserFromShortMessageMissing(update)) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateShortMessageCustom(update);
        }
    }

    private void onTLUpdateShortChatMessage(TLUpdateShortChatMessage update) {
        if (isChatMissing(update.getChatId()) || isUserMissing(update.getFromId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateShortChatMessageCustom(update);
        }
    }

    private void onTLUpdateShortSentMessage(TLUpdateShortSentMessage update) {
        onTLUpdateShortSentMessageCustom(update);
    }

    private void onTLUpdateChatParticipants(TLUpdateChatParticipants update) {
        if (isChatMissing(update.getParticipants().getChatId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChatParticipantsCustom(update);
        }
    }

    private void onTLUpdateNewMessage(TLUpdateNewMessage update) {
        if (isUserFromMessageMissing(update.getMessage())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateNewMessageCustom(update);
        }
    }

    private void onTLUpdateChannelNewMessage(TLUpdateChannelNewMessage update) {
        if (isUserFromMessageMissing(update.getMessage(), false)) {
            if (isChatMissing(update.getChannelId())) {
                differencesHandler.getDifferences();
            } else {
                final Chat channel = databaseManager.getChatById(update.getMessage().getChatId());
                if (channel != null) {
                    differencesHandler.getChannelDifferences(channel.getId(), channel.getAccessHash());
                }
            }
        } else {
            onTLUpdateChannelNewMessageCustom(update);
        }
    }

    private void onTLUpdateChannel(TLUpdateChannel update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChannelCustom(update);
        }
    }

    private void onTLUpdateBotInlineQuery(TLUpdateBotInlineQuery update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateBotInlineQueryCustom(update);
        }
    }

    private void onTLUpdateBotInlineSend(TLUpdateBotInlineSend update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateBotInlineSendCustom(update);
        }
    }

    private void onTLUpdateChannelGroup(TLUpdateChannelGroup update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChannelGroupCustom(update);
        }
    }

    private void onTLUpdateChannelMessageViews(TLUpdateChannelMessageViews update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChannelMessageViewsCustom(update);
        }
    }

    private void onTLUpdateChannelPinnedMessage(TLUpdateChannelPinnedMessage update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChannelPinnedMessageCustom(update);
        }
    }

    private void onTLUpdateChannelTooLong(TLUpdateChannelTooLong update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            final Chat channel = databaseManager.getChatById(update.getChannelId());
            if (channel != null) {
                differencesHandler.getChannelDifferences(channel.getId(), channel.getAccessHash());
            }
        }
    }

    private void onTLUpdateChatAdmin(TLUpdateChatAdmin update) {
        if (isChatMissing(update.getChatId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChatAdminCustom(update);
        }
    }

    private void onTLUpdateChatParticipantAdd(TLUpdateChatParticipantAdd update) {
        if (isChatMissing(update.getChatId()) || isUserMissing(update.getUserId()) || isUserMissing(update.getInviterId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChatParticipantAddCustom(update);
        }
    }

    private void onTLUpdateChatParticipantAdmin(TLUpdateChatParticipantAdmin update) {
        if (isChatMissing(update.getChatId()) || isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChatParticipantAdminCustom(update);
        }
    }

    private void onTLUpdateChatParticipantDelete(TLUpdateChatParticipantDelete update) {
        if (isChatMissing(update.getChatId()) || isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChatParticipantDeleteCustom(update);
        }
    }

    private void onTLUpdateChatUserTyping(TLUpdateChatUserTyping update) {
        if (isChatMissing(update.getChatId()) || isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateChatUserTypingCustom(update);
        }
    }

    private void onTLUpdateContactLink(TLUpdateContactLink update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateContactLinkCustom(update);
        }
    }

    private void onTLUpdateContactRegistered(TLUpdateContactRegistered update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateContactRegisteredCustom(update);
        }
    }

    private void onTLUpdateDcOptions(TLUpdateDcOptions update) {
        onTLUpdateDcOptionsCustom(update);
    }

    private void onTLUpdateDeleteChannelMessages(TLUpdateDeleteChannelMessages update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateDeleteChannelMessagesCustom(update);
        }
    }

    private void onTLUpdateDeleteMessages(TLUpdateDeleteMessages update) {
        onTLUpdateDeleteMessagesCustom(update);
    }

    private void onTLUpdateEditChannelMessage(TLUpdateEditChannelMessage update) {
        if (isUserFromMessageMissing(update.getMessage(), false)) {
            if (isChatMissing(update.getChannelId())) {
                differencesHandler.getDifferences();
            } else {
                final Chat channel = databaseManager.getChatById(update.getMessage().getChatId());
                if (channel != null) {
                    differencesHandler.getChannelDifferences(channel.getId(), channel.getAccessHash());
                }
            }
        } else {
            onTLUpdateEditChannelMessageCustom(update);
        }
    }

    private void onTLUpdateMessageId(TLUpdateMessageId update) {
        onTLUpdateMessageIdCustom(update);
    }

    private void onTLUpdateNewAuthorization(TLUpdateNewAuthorization update) {
        onTLUpdateNewAuthorizationCustom(update);
    }

    private void onTLUpdateNewStickerSet(TLUpdateNewStickerSet update) {
        onTLUpdateNewStickerSetCustom(update);
    }

    private void onTLUpdateNotifySettings(TLUpdateNotifySettings update) {
        if (isNotifyPeerMissing(update.getPeer())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateNotifySettingsCustom(update);
        }
    }

    private void onTLUpdatePrivacy(TLUpdatePrivacy update) {
        onTLUpdatePrivacyCustom(update);
    }

    private void onTLUpdateReadChannelInbox(TLUpdateReadChannelInbox update) {
        if (isChatMissing(update.getChannelId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateReadChannelInboxCustom(update);
        }
    }

    private void onTLUpdateReadMessagesContents(TLUpdateReadMessagesContents update) {
        onTLUpdateReadMessagesContentsCustom(update);
    }

    private void onTLUpdateReadMessagesInbox(TLUpdateReadMessagesInbox update) {
        if (isPeerMissing(update.getPeer())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateReadMessagesInboxCustom(update);
        }
    }

    private void onTLUpdateReadMessagesOutbox(TLUpdateReadMessagesOutbox update) {
        if (isPeerMissing(update.getPeer())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateReadMessagesOutboxCustom(update);
        }
    }

    private void onTLUpdateSavedGifs(TLUpdateSavedGifs update) {
        onTLUpdateSavedGifsCustom(update);
    }

    private void onTLUpdateServiceNotification(TLUpdateServiceNotification update) {
        onTLUpdateServiceNotificationCustom(update);
    }

    private void onTLUpdateStickerSets(TLUpdateStickerSets update) {
        onTLUpdateStickerSetsCustom(update);
    }

    private void onTLUpdateStickerSetsOrder(TLUpdateStickerSetsOrder update) {
        onTLUpdateStickerSetsOrderCustom(update);
    }

    private void onTLUpdateUserBlocked(TLUpdateUserBlocked update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateUserBlockedCustom(update);
        }
    }

    private void onTLUpdateUserName(TLUpdateUserName update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateUserNameCustom(update);
        }
    }

    private void onTLUpdateUserPhone(TLUpdateUserPhone update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateUserPhoneCustom(update);
        }
    }

    private void onTLUpdateUserPhoto(TLUpdateUserPhoto update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateUserPhotoCustom(update);
        }
    }

    private void onTLUpdateUserStatus(TLUpdateUserStatus update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateUserStatusCustom(update);
        }
    }

    private void onTLUpdateUserTyping(TLUpdateUserTyping update) {
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateUserTypingCustom(update);
        }
    }

    private void onTLUpdateWebPage(TLUpdateWebPage update) {
        onTLUpdateWebPageCustom(update);
    }

    private void onTLUpdateBotCallbackQuery(TLUpdateBotCallbackQuery update) {
        if (isUserMissing(update.getUserId()) || isPeerMissing(update.getPeer())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateBotCallbackQueryCustom(update);
        }
    }

    private void onTLUpdateEditMessage(TLUpdateEditMessage update){
        if (isUserFromMessageMissing(update.getMessage())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateEditMessageCustom(update);
        }
    }

    private void onTLUpdateInlineBotCallbackQuery(TLUpdateInlineBotCallbackQuery update){
        if (isUserMissing(update.getUserId())) {
            differencesHandler.getDifferences();
        } else {
            onTLUpdateInlineBotCallbackQueryCustom(update);
        }
    }

    @Override
    public final void updateStateModification(TLUpdatesState state) {
        differencesHandler.updateStateModification(state, false);
    }

    @Override
    public final void onTLUpdatesTooLong() {
        differencesHandler.getDifferences();
    }

    private void onTLFakeUpdate(TLFakeUpdate update) {
        onTLFakeUpdateCustom(update);
    }

    @Override
    public final void onTLAbsDifference(@NotNull TLAbsDifference absDifference) {
        onUsers(absDifference.getUsers());
        onChats(absDifference.getChats());
        absDifference.getNewMessages().stream().forEach(this::onTLAbsMessageCustom);
        absDifference.getOtherUpdates().stream().map(x -> {
            UpdateWrapper updateWrapper = new UpdateWrapper(x);
            updateWrapper.disablePtsCheck();
            updateWrapper.disableUpdatePts();
            return updateWrapper;
        }).forEach(this::processUpdate);
    }

    @Override
    public final void onTLChannelDifferences(List<TLAbsUser> users, List<TLAbsMessage> messages, List<TLAbsUpdate> newUpdates, List<TLAbsChat> chats) {
        onUsers(users);
        onChats(chats);
        messages.stream().forEach(this::onTLAbsMessageCustom);
        newUpdates.stream().map(x -> {
            UpdateWrapper updateWrapper = new UpdateWrapper(x);
            updateWrapper.disablePtsCheck();
            updateWrapper.disableUpdatePts();
            return updateWrapper;
        }).forEach(this::processUpdate);
    }

    @Override
    public final void onUsers(List<TLAbsUser> users) {
        onUsersCustom(users);
    }

    @Override
    public final void onChats(List<TLAbsChat> chats) {
        onChatsCustom(chats);
    }

    private boolean isUserFromMessageMissing(TLAbsMessage message, boolean checkChatId) {
        boolean isMissing = true;

        if (message instanceof TLMessage) {
            final TLMessage tlMessage = (TLMessage) message;
            boolean isFromMissing = true;
            if (tlMessage.hasFromId()) {
                isFromMissing = isUserMissing(tlMessage.getFromId());
            }

            boolean isToMissing = true;
            if (tlMessage.getToId() instanceof TLPeerUser) {
                isToMissing = isUserMissing(tlMessage.getToId().getId());
            } else if (checkChatId) {
                isToMissing = isChatMissing(tlMessage.getChatId());
            }

            boolean isForwardedMissing = true;
            if (tlMessage.isForwarded()) {
                isForwardedMissing = isUserMissing(tlMessage.getFwdFrom().getFromId());
            }

            isMissing = isFromMissing && isToMissing && isForwardedMissing;
        } else if (message instanceof TLMessageService ){
            final TLMessageService tlMessageService = (TLMessageService) message;

            boolean isFromMissing = true;
            if (tlMessageService.hasFromId()) {
                isFromMissing = isUserMissing(tlMessageService.getFromId());
            }

            boolean isToMissing = true;
            if (tlMessageService.getToId() instanceof TLPeerUser) {
                isToMissing = isUserMissing(tlMessageService.getToId().getId());
            } else if (checkChatId) {
                isToMissing = isChatMissing(tlMessageService.getChatId());
            }

            isMissing = isFromMissing && isToMissing;
        }

        return isMissing;

    }

    private boolean isUserFromMessageMissing(TLAbsMessage message) {
        return isUserFromMessageMissing(message, false);
    }

    private boolean isChatMissing(int chatId) {
        return databaseManager.getChatById(chatId) == null;
    }

    private boolean isUserMissing(int userId) {
        return databaseManager.getUserById(userId) == null;
    }

    private boolean isPeerMissing(TLAbsPeer peer) {
        final boolean isMissing;
        if (peer instanceof TLPeerUser) {
            isMissing = databaseManager.getUserById(peer.getId()) == null;
        } else {
            isMissing = databaseManager.getChatById(peer.getId()) == null;
        }
        return isMissing;
    }

    private boolean isNotifyPeerMissing(TLAbsNotifyPeer notifyPeer) {
        boolean isMissing = false;
        if (notifyPeer instanceof TLNotifyPeer) {
            isMissing = isPeerMissing(((TLNotifyPeer) notifyPeer).getPeer());
        }

        return isMissing;
    }

    /**
     * Check if all user needed by a updateShortMessage are not present in database
     * @param updateShortMessage Update to check
     * @return true if any of them is missing, false otherwise
     */
    private boolean isUserFromShortMessageMissing(@NotNull TLUpdateShortMessage updateShortMessage) {
        return (databaseManager.getUserById(updateShortMessage.getUserId()) == null) ||
                (updateShortMessage.isForwarded() && (databaseManager.getUserById(updateShortMessage.getFwdFrom().getFromId()) == null));
    }

    protected abstract void onTLUpdateChatParticipantsCustom(TLUpdateChatParticipants update);
    protected abstract void onTLUpdateNewMessageCustom(TLUpdateNewMessage update);
    protected abstract void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update);
    protected abstract void onTLUpdateChannelCustom(TLUpdateChannel update);
    protected abstract void onTLUpdateBotInlineQueryCustom(TLUpdateBotInlineQuery update);
    protected abstract void onTLUpdateBotInlineSendCustom(TLUpdateBotInlineSend update);
    protected abstract void onTLUpdateChannelGroupCustom(TLUpdateChannelGroup update);
    protected abstract void onTLUpdateChannelMessageViewsCustom(TLUpdateChannelMessageViews update);
    protected abstract void onTLUpdateChannelPinnedMessageCustom(TLUpdateChannelPinnedMessage update);
    protected abstract void onTLUpdateChatAdminCustom(TLUpdateChatAdmin update);
    protected abstract void onTLUpdateChatParticipantAddCustom(TLUpdateChatParticipantAdd update);
    protected abstract void onTLUpdateChatParticipantAdminCustom(TLUpdateChatParticipantAdmin update);
    protected abstract void onTLUpdateChatParticipantDeleteCustom(TLUpdateChatParticipantDelete update);
    protected abstract void onTLUpdateChatUserTypingCustom(TLUpdateChatUserTyping update);
    protected abstract void onTLUpdateContactLinkCustom(TLUpdateContactLink update);
    protected abstract void onTLUpdateContactRegisteredCustom(TLUpdateContactRegistered update);
    protected abstract void onTLUpdateDcOptionsCustom(TLUpdateDcOptions update);
    protected abstract void onTLUpdateDeleteChannelMessagesCustom(TLUpdateDeleteChannelMessages update);
    protected abstract void onTLUpdateDeleteMessagesCustom(TLUpdateDeleteMessages update);
    protected abstract void onTLUpdateEditChannelMessageCustom(TLUpdateEditChannelMessage update);
    protected abstract void onTLUpdateMessageIdCustom(TLUpdateMessageId update);
    protected abstract void onTLUpdateNewAuthorizationCustom(TLUpdateNewAuthorization update);
    protected abstract void onTLUpdateNewStickerSetCustom(TLUpdateNewStickerSet update);
    protected abstract void onTLUpdateNotifySettingsCustom(TLUpdateNotifySettings update);
    protected abstract void onTLUpdatePrivacyCustom(TLUpdatePrivacy update);
    protected abstract void onTLUpdateReadChannelInboxCustom(TLUpdateReadChannelInbox update);
    protected abstract void onTLUpdateReadMessagesContentsCustom(TLUpdateReadMessagesContents update);
    protected abstract void onTLUpdateReadMessagesInboxCustom(TLUpdateReadMessagesInbox update);
    protected abstract void onTLUpdateReadMessagesOutboxCustom(TLUpdateReadMessagesOutbox update);
    protected abstract void onTLUpdateSavedGifsCustom(TLUpdateSavedGifs update);
    protected abstract void onTLUpdateServiceNotificationCustom(TLUpdateServiceNotification update);
    protected abstract void onTLUpdateStickerSetsCustom(TLUpdateStickerSets update);
    protected abstract void onTLUpdateStickerSetsOrderCustom(TLUpdateStickerSetsOrder update);
    protected abstract void onTLUpdateUserBlockedCustom(TLUpdateUserBlocked update);
    protected abstract void onTLUpdateUserNameCustom(TLUpdateUserName update);
    protected abstract void onTLUpdateUserPhoneCustom(TLUpdateUserPhone update);
    protected abstract void onTLUpdateUserPhotoCustom(TLUpdateUserPhoto update);
    protected abstract void onTLUpdateUserStatusCustom(TLUpdateUserStatus update);
    protected abstract void onTLUpdateUserTypingCustom(TLUpdateUserTyping update);
    protected abstract void onTLUpdateWebPageCustom(TLUpdateWebPage update);
    protected abstract void onTLFakeUpdateCustom(TLFakeUpdate update);
    protected abstract void onTLUpdateShortMessageCustom(TLUpdateShortMessage update);
    protected abstract void onTLUpdateShortChatMessageCustom(TLUpdateShortChatMessage update);
    protected abstract void onTLUpdateShortSentMessageCustom(TLUpdateShortSentMessage update);
    protected abstract void onTLUpdateBotCallbackQueryCustom(TLUpdateBotCallbackQuery update);
    protected abstract void onTLUpdateEditMessageCustom(TLUpdateEditMessage update);
    protected abstract void onTLUpdateInlineBotCallbackQueryCustom(TLUpdateInlineBotCallbackQuery update);
    protected abstract void onTLAbsMessageCustom(TLAbsMessage message);
    protected abstract void onUsersCustom(List<TLAbsUser> users);
    protected abstract void onChatsCustom(List<TLAbsChat> chats);
}

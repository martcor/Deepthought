package org.telegram.api.messages.dialogs;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.dialog.TLAbsDialog;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.StreamingUtils;
import org.telegram.tl.TLContext;
import org.telegram.tl.TLVector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type TL dialogs slice.
 */
public class TLDialogsSlice extends TLAbsDialogs {
    /**
     * The constant CLASS_ID.
     */
    public static final int CLASS_ID = 0x71e094f3;

    private int count;

    /**
     * Instantiates a new TL dialogs slice.
     */
    public TLDialogsSlice() {
        super();
    }

    public int getClassId() {
        return CLASS_ID;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Sets count.
     *
     * @param value the value
     */
    public void setCount(int value) {
        this.count = value;
    }

    public void serializeBody(OutputStream stream)
            throws IOException {
        StreamingUtils.writeInt(this.count, stream);
        StreamingUtils.writeTLVector(this.dialogs, stream);
        StreamingUtils.writeTLVector(this.messages, stream);
        StreamingUtils.writeTLVector(this.chats, stream);
        StreamingUtils.writeTLVector(this.users, stream);
    }

    public void deserializeBody(InputStream stream, TLContext context)
            throws IOException {
        this.count = StreamingUtils.readInt(stream);
        this.dialogs = (TLVector<TLAbsDialog>) StreamingUtils.readTLVector(stream, context);
        this.messages = (TLVector<TLAbsMessage>) StreamingUtils.readTLVector(stream, context);
        this.chats = (TLVector<TLAbsChat>) StreamingUtils.readTLVector(stream, context);
        this.users = (TLVector<TLAbsUser>) StreamingUtils.readTLVector(stream, context);
    }

    public String toString() {
        return "messages.dialogsSlice#71e094f3";
    }
}
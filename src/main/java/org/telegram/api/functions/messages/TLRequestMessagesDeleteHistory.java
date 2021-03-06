package org.telegram.api.functions.messages;

import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.messages.TLAffectedHistory;
import org.telegram.tl.StreamingUtils;
import org.telegram.tl.TLContext;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type TL request messages delete history.
 */
public class TLRequestMessagesDeleteHistory extends TLMethod<TLAffectedHistory> {
    /**
     * The constant CLASS_ID.
     */
    public static final int CLASS_ID = 0xb7c13bd9;

    private TLAbsInputPeer peer;
    private int maxId;

    /**
     * Instantiates a new TL request messages delete history.
     */
    public TLRequestMessagesDeleteHistory() {
        super();
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public TLAffectedHistory deserializeResponse(InputStream stream, TLContext context)
            throws IOException {
        final TLObject res = StreamingUtils.readTLObject(stream, context);
        if (res == null) {
            throw new IOException("Unable to parse response");
        }
        if ((res instanceof TLAffectedHistory)) {
            return (TLAffectedHistory) res;
        }
        throw new IOException("Incorrect response type. Expected " + TLAffectedHistory.class.getCanonicalName() + ", got: " + res.getClass().getCanonicalName());
    }

    /**
     * Gets peer.
     *
     * @return the peer
     */
    public TLAbsInputPeer getPeer() {
        return this.peer;
    }

    /**
     * Sets peer.
     *
     * @param value the value
     */
    public void setPeer(TLAbsInputPeer value) {
        this.peer = value;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    public void serializeBody(OutputStream stream)
            throws IOException {
        StreamingUtils.writeTLObject(this.peer, stream);
        StreamingUtils.writeInt(this.maxId, stream);
    }

    public void deserializeBody(InputStream stream, TLContext context)
            throws IOException {
        this.peer = ((TLAbsInputPeer) StreamingUtils.readTLObject(stream, context));
        this.maxId = StreamingUtils.readInt(stream);
    }

    public String toString() {
        return "messages.deleteHistory#b7c13bd9";
    }
}
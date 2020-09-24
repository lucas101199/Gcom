package se.cs.umu.gcom.communication.messages;

import se.cs.umu.gcom.communication.MessageType;
import se.cs.umu.gcom.group.Peer;

import java.io.Serializable;
import java.util.UUID;

public class GcomMessage<T extends Serializable> extends BasicMessage {

    private final T content;

    public GcomMessage(UUID groupId, Peer sender, T content) {
        super(groupId, sender);
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    @Override
    public MessageType getType() {
        return MessageType.CONTENT;
    }

    @Override
    public String toString() {
        return "GcomMessage: " + content;
    }
}

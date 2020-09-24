package se.cs.umu.gcom.communication.messages;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.group.Peer;

import java.io.Serializable;
import java.util.UUID;

public abstract class BasicMessage implements Message, Serializable {

    private final UUID messageId, groupId;
    private Peer sender;
    private final Peer creator;

    public BasicMessage(UUID groupId, Peer creator) {
        this.messageId = UUID.randomUUID();
        this.groupId = groupId;
        this.sender = creator;
        this.creator = creator;
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public UUID getGroupId() {
        return groupId;
    }

    @Override
    public Peer getSender() {
        return sender;
    }

    @Override
    public void setSender(Peer peer) {
        this.sender = peer;
    }

    @Override
    public Peer getCreator() {
        return creator;
    }
}

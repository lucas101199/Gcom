package se.cs.umu.gcom.communication.messages;

import se.cs.umu.gcom.communication.MessageType;
import se.cs.umu.gcom.group.Peer;

import java.util.UUID;

public class GroupJoinMessage extends BasicMessage {

    public GroupJoinMessage(UUID groupId, Peer sender) {
        super(groupId, sender);
    }

    @Override
    public MessageType getType() {
        return MessageType.GROUP_JOIN;
    }

    @Override
    public String toString() {
        return "GroupJoinMessage: " + getCreator().getName() + " joined group";
    }
}

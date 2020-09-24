package se.cs.umu.gcom.communication.messages;

import se.cs.umu.gcom.communication.MessageType;
import se.cs.umu.gcom.group.Peer;

import java.util.UUID;

public class GroupLeftMessage extends BasicMessage {


    public GroupLeftMessage(UUID groupId, Peer sender) {
        super(groupId, sender);
    }

    @Override
    public MessageType getType() {
        return MessageType.GROUP_LEFT;
    }

    @Override
    public String toString() {
        return "GroupLeftMessage: " + getCreator().getName() + " left group";
    }
}

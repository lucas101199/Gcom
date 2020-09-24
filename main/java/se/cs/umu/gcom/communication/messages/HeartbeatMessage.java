package se.cs.umu.gcom.communication.messages;

import se.cs.umu.gcom.communication.MessageType;
import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;

import java.util.UUID;

public class HeartbeatMessage extends BasicMessage {

    private final Group group;

    public HeartbeatMessage(UUID groupId, Peer sender, Group group) {
        super(groupId, sender);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public MessageType getType() {
        return null;
    }
}

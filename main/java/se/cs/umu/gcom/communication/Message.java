package se.cs.umu.gcom.communication;

import se.cs.umu.gcom.group.Peer;

import java.util.UUID;

public interface Message {
    UUID getMessageId();
    UUID getGroupId();
    Peer getSender();
    void setSender(Peer peer);
    Peer getCreator();
    MessageType getType();
}

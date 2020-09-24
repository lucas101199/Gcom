package se.cs.umu.gcom.misc;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.ordering.Ordering;

import java.io.Serializable;

public interface GcomListener<T extends Serializable> {

    void onGroupJoined(Group group);
    void onMemberAdded(Group group, Peer peer);
    void onMemberRemove(Group group, Peer peer);
    void onMemberMessage(Group group, Peer sender, T content);


    //Debug listeners - normally shouldn't do anything.
    default void onMessageReceived(Group group, Message message, Ordering ordering) {}
    default void onMessageSend(Group group, Message message, Ordering ordering) {}






















}

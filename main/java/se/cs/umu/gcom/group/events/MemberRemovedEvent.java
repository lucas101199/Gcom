package se.cs.umu.gcom.group.events;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;

public class MemberRemovedEvent extends PeerGroupEvent {

    private RemoveReason reason;

    public MemberRemovedEvent(Group eventGroup, Peer eventPeer, RemoveReason reason) {
        super(eventGroup, eventPeer);
        this.reason = reason;
    }

    public RemoveReason getReason() {
        return reason;
    }

}

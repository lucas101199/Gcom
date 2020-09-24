package se.cs.umu.gcom.group.events;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;

public class PeerGroupEvent extends GroupEvent implements PeerEvent {

    private final Peer eventPeer;

    public PeerGroupEvent(Group eventGroup, Peer eventPeer) {
        super(eventGroup);
        this.eventPeer = eventPeer;
    }

    @Override
    public Peer getEventPeer() {
        return eventPeer;
    }
}

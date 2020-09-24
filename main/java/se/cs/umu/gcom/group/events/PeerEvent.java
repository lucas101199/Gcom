package se.cs.umu.gcom.group.events;

import se.cs.umu.gcom.group.Peer;

public interface PeerEvent extends Event {
    Peer getEventPeer();
}

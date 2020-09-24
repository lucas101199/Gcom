package se.cs.umu.gcom.group.events;

import se.cs.umu.gcom.group.Group;

public class HeartbeatEvent extends GroupEvent {
    public HeartbeatEvent(Group eventGroup) {
        super(eventGroup);
    }
}

package se.cs.umu.gcom.group.events;

import se.cs.umu.gcom.group.Group;

public class GroupEvent implements Event {

    private final Group group;

    public GroupEvent(Group eventGroup) {
        this.group = eventGroup;
    }

    @Override
    public Group getGroup() {
        return group;
    }
}

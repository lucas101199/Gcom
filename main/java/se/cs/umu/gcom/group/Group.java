package se.cs.umu.gcom.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {

    private final UUID id;
    private final String name;

    final List<Peer> members = new ArrayList<>();

    public Group(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<Peer> getMembers() {
        //return Collections.unmodifiableList(members);
        return members;
}

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}

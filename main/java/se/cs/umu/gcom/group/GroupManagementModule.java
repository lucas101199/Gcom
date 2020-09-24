package se.cs.umu.gcom.group;


import se.cs.umu.gcom.communication.Receiver;
import se.cs.umu.gcom.group.events.Event;
import se.cs.umu.gcom.group.events.MemberRemovedEvent;
import se.cs.umu.gcom.group.events.RemoveReason;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.function.Consumer;


/**
 * The group management module.
 * Exposes group methods to Gcom and has a crash detection thread that can be started.
 */
public class GroupManagementModule {

    private final Peer self;
    private final Consumer<Event> groupEventHandler;
    private final Map<UUID,Group> groupMap = new HashMap<>();

    private final Timer crashModule = new Timer();

    public GroupManagementModule(Peer self, Consumer<Event> groupEventHandler) {
        this.self = self;
        this.groupEventHandler = groupEventHandler;
    }

    public Group getGroup(UUID id) {
        return groupMap.get(id);
    }

    public Group createGroup(String name) {
        if(groupMap.values().stream().anyMatch((g) -> g.getName().equals(name))) {
            return null;
        }
        Group g = new Group(UUID.randomUUID(), name);
        g.members.add(self);
        addGroup(g);
        return g;
    }

    public Collection<Group> getGroups() {
        return groupMap.values();
    }

    public void removeGroup(UUID id) {
        Group g = groupMap.get(id);
        if(g != null) {
            groupMap.remove(id);
        }
    }

    public void addGroup(Group g) {
        groupMap.put(g.getId(),g);
    }

    public void addMember(Group g, Peer member) {
        if(!g.members.contains(member)) {
            g.members.add(member);
        }

    }

    public void removeMember(Group g, Peer member) {
        g.members.remove(member);
    }

    public void startCrashModule() {
        crashModule.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Group> groups = new ArrayList<>(groupMap.values());
                for(Group g : groups) {
                    crashCheck(g);
                }
            }
        }, 0, 4000);
    }

    public void shutdownCrashModule() {
        crashModule.cancel();
    }

    private void crashCheck(Group group) {
        List<Peer> members = new ArrayList<>(group.getMembers());
        for(Peer p : members) {
            try {
                Registry registry = LocateRegistry.getRegistry(p.getIp());
                Receiver stub = (Receiver)registry.lookup(p.getUUID().toString());
                stub.heartbeat();
            } catch(RemoteException | NotBoundException e) {
                //Detected crash of member
                removeMember(group,p);
                groupEventHandler.accept(new MemberRemovedEvent(group,p, RemoveReason.CRASHED));
            }
        }
    }
}

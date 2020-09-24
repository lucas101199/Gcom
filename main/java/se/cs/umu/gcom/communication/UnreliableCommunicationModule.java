package se.cs.umu.gcom.communication;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.UUID;

public class UnreliableCommunicationModule implements CommunicationModule {

    private final Peer self;

    public UnreliableCommunicationModule(Peer self) {
        this.self = self;
    }

    @Override
    public boolean multicast(List<Peer> targets, VectorClock clock, Message m) {
        if(clock != null) {
            clock.tick();
        }
        for(Peer p : targets) {
            message(p.getIp(),p.getUUID().toString(),clock,m);
        }
        return true;
    }

    @Override
    public boolean receive(Group g, VectorClock clock, Message message) {
        if(message.getCreator().equals(self)) {
            return false;
        }
        return true;
    }

    @Override
    public Group getGroupInfo(String ip, String peerId, UUID groupId) {
        try {
            Registry registry = LocateRegistry.getRegistry(ip);
            Receiver stub = (Receiver)registry.lookup(peerId);
            return stub.getGroupInfo(groupId);
        } catch (RemoteException | NotBoundException ignored) {}
        return null;
    }
}

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

public interface CommunicationModule {

    boolean multicast(List<Peer> targets, VectorClock clock, Message m);
    boolean receive(Group g, VectorClock clock, Message message);
    Group getGroupInfo(String ip, String peerId, UUID groupId);

    default boolean message(String ip, String uuidString, VectorClock clock, Message m) {
        try {
            Registry registry = LocateRegistry.getRegistry(ip);
            Receiver stub = (Receiver)registry.lookup(uuidString);
            stub.receive(clock,m);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

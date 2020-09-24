package se.cs.umu.gcom.communication;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ReliableCommunicationModule implements CommunicationModule {

    private final Peer self;

    private HashSet<UUID> received = new HashSet<>();

    public ReliableCommunicationModule(Peer self) {
        this.self = self;
    }

    /**
     * External multicast
     * Ticks clock and sends to everyone specified.
     * @param targets - targets to send to, should include itself.
     * @param clock - The vector clock.
     * @param m - the message
     * @return true
     */
    @Override
    public boolean multicast(List<Peer> targets, VectorClock clock, Message m) {
        if(clock != null) {
            clock.tick();
        }
        return multicast(targets,clock,m,false);
    }

    /**
     * Internal multicast, with option to exclude sending to themselves.
     */
    private boolean multicast(List<Peer> targets, VectorClock clock, Message m, boolean excludeSelf) {
        //Send to self first.
        if(!excludeSelf && targets.contains(self)) {
            message(self.getIp(),self.getUUID().toString(),clock,m);
        }

        //Send to others after.
        for(Peer p : targets) {
            if(p.equals(self)) {
                continue;
            }
            message(p.getIp(),p.getUUID().toString(),clock,m);
        }
        return true;
    }

    /**
     * Used to ask other peers about group information
     * @param ip - the ip of the peer to ask
     * @param peerId - the peer id of the peer to ask
     * @param groupId - the group id to ask for information about.
     * @return Group object or null if failure.
     */
    public Group getGroupInfo(String ip, String peerId, UUID groupId) {
        try {
            Registry registry = LocateRegistry.getRegistry(ip);
            Receiver stub = (Receiver)registry.lookup(peerId);
            return stub.getGroupInfo(groupId);
        } catch (RemoteException| NotBoundException ignored) {}
        return null;
    }

    /**
     * Receive a message, and return if it should be further delivered.
     * @param g - the group the message belongs to
     * @param clock - the vector clock associated with the message.
     * @param message - the message
     * @return true if should be further delivered.
     */
    @Override
    public boolean receive(Group g, VectorClock clock, Message message) {
        if(received.contains(message.getMessageId())) {
            return false;
        }
        received.add(message.getMessageId());
        if(message.getCreator().equals(self)) {
            return false;
        }
        Peer previousSender = message.getSender();
        message.setSender(self);
        multicast(g.getMembers(),clock,message,true);
        message.setSender(previousSender);
        return true;
    }

}

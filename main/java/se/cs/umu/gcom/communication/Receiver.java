package se.cs.umu.gcom.communication;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Receiver extends Remote {
     void receive(VectorClock clock, Message message) throws RemoteException;
     Group getGroupInfo(UUID groupId) throws RemoteException;
     boolean heartbeat() throws RemoteException;
}

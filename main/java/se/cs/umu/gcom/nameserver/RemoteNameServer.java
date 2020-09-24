package se.cs.umu.gcom.nameserver;

import se.cs.umu.gcom.group.Group;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

/*
    Class that will be call in the GMM
    for adding a new group create by a peer
    in the se.cs.umu.gcom.nameserver.ServerName
 */

public interface RemoteNameServer extends Remote {

    boolean addGroup(Group group) throws RemoteException;
    List<Group> getGroups() throws RemoteException;
}

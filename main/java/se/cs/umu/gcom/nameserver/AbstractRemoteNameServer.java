package se.cs.umu.gcom.nameserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class AbstractRemoteNameServer extends UnicastRemoteObject implements RemoteNameServer {

    protected AbstractRemoteNameServer() throws RemoteException {
    }
}

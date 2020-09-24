package se.cs.umu.gcom.communication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class GcomReceiver extends UnicastRemoteObject implements Receiver {
    protected GcomReceiver() throws RemoteException {}
}

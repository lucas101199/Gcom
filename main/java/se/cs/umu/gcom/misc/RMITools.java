package se.cs.umu.gcom.misc;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMITools {

    private static Registry registry;

    public static void main(String[] args) {
        registry = RMITools.createOrGet(1099);
        while(true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public static Registry createOrGet(int port) {
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ignored) {}
        if(registry != null) {
            return registry;
        }
        try {
            registry = LocateRegistry.getRegistry(1099);
        } catch (RemoteException ignored) {}
        return registry;
    }




}

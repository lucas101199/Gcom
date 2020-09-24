package se.cs.umu.gcom.nameserver;

import se.cs.umu.gcom.communication.Receiver;
import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.misc.RMITools;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class NameServer {

    private final Map<UUID, Group> groupMap = new HashMap<>();
    Registry registry = null;

    public NameServer() {}

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("IP: " + InetAddress.getLocalHost().getHostAddress());
        NameServer nameServer = new NameServer();
        try {
            nameServer.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void start() throws RemoteException {
        //Registry
        registry = RMITools.createOrGet(1099);
        RemoteNameServer impl = new AbstractRemoteNameServer() {
            @Override
            public boolean addGroup(Group group){
                groupMap.put(group.getId(),group);
                return true;
            }

            @Override
            public List<Group> getGroups() {
                return new ArrayList<>(groupMap.values());
            }
        };
        registry.rebind("NameServer", impl);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                registry.unbind("NameServer");
            } catch (Exception ignored) {}
        }));

        //Loop that checks groups every 5 seconds
        while(true) {
            checkGroups();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void checkGroups() {
        List<Group> groups = new ArrayList<>(groupMap.values());
        for(Group g : groups) {
            List<Peer> peers = new ArrayList<>(g.getMembers());
            System.out.println("Group: " + g.getName() + " - " + g.getId().toString() + " - " + peers.size());
            boolean updated = false;
            for(Peer p : peers) {
                //If succceed to update group info through peer
                if(updateGroupInfo(g, p)) {
                    //Exit and get next group.
                    updated = true;
                    break;
                }
            }
            //If a group failed to get updated(no peers alive), we remove it from the name server.
            if(!updated) {
                groupMap.remove(g.getId());
            }
        }
    }

    private boolean updateGroupInfo(Group g, Peer p) {
        Group group = getGroupInfo(p.getIp(),p.getUUID().toString(), g.getId());
        if(group == null) {
            return false;
        }
        groupMap.put(g.getId(),group);
        return true;
    }

    private Group getGroupInfo(String ip, String peerId, UUID groupId) {
        try {
            Registry registry = LocateRegistry.getRegistry(ip);
            Receiver stub = (Receiver)registry.lookup(peerId);
            return stub.getGroupInfo(groupId);
        } catch (RemoteException | NotBoundException ignored) {
        }
        return null;
    }

}

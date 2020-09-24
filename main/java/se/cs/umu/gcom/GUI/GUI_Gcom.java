package se.cs.umu.gcom.GUI;

import se.cs.umu.gcom.Gcom;
import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.misc.GcomListener;
import se.cs.umu.gcom.nameserver.RemoteNameServer;

import javax.swing.*;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class GUI_Gcom implements GcomListener<String> {
    Gcom<String> gcom;
    GUI gui;
    final String nameServerIp;

    public GUI_Gcom(String name, String nameServerIp) throws UnknownHostException, RemoteException {
        this.gcom = new Gcom<>(name);
        this.gui = new GUI(name, this);
        this.nameServerIp = nameServerIp;
        gcom.setEventListener(this);
        gcom.start();
    }

    public Group create_group(String name) {
        return gcom.createGroup(name);
    }

    public List<Group> getNameServerGroups() {
        try {
            Registry registry = LocateRegistry.getRegistry(nameServerIp, 1099);
            RemoteNameServer stub = (RemoteNameServer)registry.lookup("NameServer");
            return stub.getGroups();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void display_Group(Collection<Group> groups) {
        if(groups == null) {
            return;
        }
        for (Group group : groups) {
            this.gui.displayGroup(group);
            System.out.println(group.getId().toString());
        }
    }

    public boolean join_group(String name) {
        //Fetch all groups from name server.
        List<Group> groups = getNameServerGroups();
        //Try to find the group with the given name
        Optional<Group> group = groups.stream().filter((g) -> g.getName().equals(name)).findFirst();
        //Join the group if group found and it doesnt fail.
        return group.map(gr -> gcom.joinGroup(gr.getId(), gr.getMembers())).orElse(false);
    }

    public boolean leave_group(Group group) {
        gcom.leaveGroup(group.getId());  //line where the bug is
        gui.removeGroup(group);
        gui.panelGroup.updateUI();
        gui.panelMember.removeAll();
        JLabel member = new JLabel("Members");
        gui.panelMember.add(member);
        gui.panelMember.updateUI();
        gui.setCurrentGroup(null);
        gui.card.first(gui.message);
        gui.message.setVisible(true);
        return true;
    }

    public boolean send(Group group, String message) {
        return gcom.send(group.getId(),message);
    }

    @Override
    public void onMemberAdded(Group group, Peer peer) {
        //Whenever someone joins a group you are in.
        System.out.println(peer.getName() + " has joined.");
        SwingUtilities.invokeLater(() -> {
            gui.addMessage(group,peer.getName() + " has joined.");
        });

        if(gui.currentGroup != null && gui.currentGroup.getId().equals(group.getId())) {
            SwingUtilities.invokeLater(() -> {
                gui.addMember(peer);
            });
        }
    }

    @Override
    public void onMemberRemove(Group group, Peer peer) {
        //Whenever someone leaves or crashes this is called.
        System.out.println(peer.getName() + " has left.");
        SwingUtilities.invokeLater(() -> {
            gui.addMessage(group,peer.getName() + " has left.");
        });

        if(gui.currentGroup != null && gui.currentGroup.getId().equals(group.getId())) {
            SwingUtilities.invokeLater(() -> {
                gui.refreshMembersList(group);
            });
        }
    }

    @Override
    public void onMemberMessage(Group group, Peer sender, String content) {
        //This is triggered everytime someone sends a message including yourself.
        System.out.println(sender.getName() + ": " + content);
        SwingUtilities.invokeLater(() -> {
            gui.addMessage(group,sender.getName() + ": " + content);
        });
    }

    @Override
    public void onGroupJoined(Group group) {
        //When you join a group, this is probably not necessary at all.
    }
}

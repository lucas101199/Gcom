package se.cs.umu.gcom.GUI;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.nameserver.RemoteNameServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

public class GUI_eventHandlerCreation implements GUI_eventHandler {

    GUI_Gcom gui_gcom;

    public GUI_eventHandlerCreation(GUI_Gcom gui_gcom) {
        this.gui_gcom = gui_gcom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = JOptionPane.showInputDialog(
                "Name of the group", null);
        System.out.println(name);
        if(name == null) {
            return;
        }

        Group g = gui_gcom.create_group(name);

        try {
            Registry registry = LocateRegistry.getRegistry(gui_gcom.nameServerIp, 1099);
            RemoteNameServer stub = (RemoteNameServer)registry.lookup("NameServer");
            stub.addGroup(g);
        } catch (RemoteException | NotBoundException ignored) {}

        Collection<Group> list_group = gui_gcom.gcom.getJoinedGroups();
        System.out.println(list_group.size());
        gui_gcom.display_Group(list_group);
    }
}

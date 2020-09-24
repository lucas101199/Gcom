package se.cs.umu.gcom.GUI;

import se.cs.umu.gcom.group.Group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.UUID;

public class GUI_eventHandlerJoin implements GUI_eventHandler {

    GUI_Gcom gui_gcom;

    public GUI_eventHandlerJoin(GUI_Gcom gui_gcom) {
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

        boolean join = gui_gcom.join_group(name);

        if (join) {
            Collection<Group> list_group = gui_gcom.gcom.getJoinedGroups();
            gui_gcom.display_Group(list_group);
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "Can't join the group something went wrong");
        }
    }
}

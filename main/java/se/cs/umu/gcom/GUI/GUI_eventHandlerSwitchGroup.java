package se.cs.umu.gcom.GUI;

import se.cs.umu.gcom.group.Group;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class GUI_eventHandlerSwitchGroup implements GUI_eventHandler {

    public GUI_Gcom gui_gcom;
    public Group group;
    public ArrayList<JPanel> panel;

    public GUI_eventHandlerSwitchGroup(GUI_Gcom gui_gcom, Group group, ArrayList<JPanel> panel_group) {
        this.gui_gcom = gui_gcom;
        this.group = group;
        this.panel = panel_group;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SetPanelVisible(group.getId().toString());
        gui_gcom.gui.setCurrentGroup(group);
        gui_gcom.gui.refreshMembersList(group);
    }

    //put only the panel(group) active to visible = true
    public void SetPanelVisible(String UUID) {
        for (JPanel pane : panel) {
            if (pane.getName().equals(UUID)) {
                gui_gcom.gui.card.show(gui_gcom.gui.message, UUID);
            }
        }
    }
}

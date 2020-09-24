package se.cs.umu.gcom.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GUI_eventHandlerLeave implements GUI_eventHandler{

    GUI_Gcom gui_gcom;

    public GUI_eventHandlerLeave(GUI_Gcom gui_gcom) {
        this.gui_gcom = gui_gcom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui_gcom.leave_group(gui_gcom.gui.currentGroup)) {
            JOptionPane.showMessageDialog(gui_gcom.gui.frame,
                    "You just left the group");
        }
        else {
            JOptionPane.showMessageDialog(gui_gcom.gui.frame,
                    "Something went wrong impossible to leave the group");
        }
    }
}

package se.cs.umu.gcom.GUI;

import se.cs.umu.gcom.group.Group;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GUI_eventHandlerSend implements GUI_eventHandler {

    GUI_Gcom gui_gcom;
    JTextField message;

    public GUI_eventHandlerSend(GUI_Gcom gui_gcom, JTextField message) {
        this.gui_gcom = gui_gcom;
        this.message = message;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String data_message =  message.getText();
        Group currentGroup = gui_gcom.gui.currentGroup;
        if(currentGroup != null && !data_message.isEmpty()) {
            gui_gcom.send(currentGroup,data_message);
        }
    }
}

package se.cs.umu.gcom.GUI;

import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class GUI extends Frame{

    public JButton Create_group;
    public JFrame frame;
    public JPanel panelGroup;
    public JPanel panelMember;
    public GridBagConstraints gbc, gbc_member;
    public GUI_Gcom gui_gcom;
    public JButton Join_group;
    public JPanel message;
    public ArrayList<JPanel> panel_group; //each group has it own panel
    public CardLayout card;
    public Group currentGroup = null;

    public GUI(String name_user, GUI_Gcom gui_gcom) {
        panel_group = new ArrayList<>();
        Border blackLine = BorderFactory.createLineBorder(Color.BLACK);

        //Creating the Frame
        frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        //---------------------------------- BEGIN LEFT PANEL GROUP -----------------------------------//

        //Creating the panel for all the groups
        gbc = new GridBagConstraints();
        panelGroup = new JPanel();
        panelGroup.setLayout(new GridBagLayout());
        panelGroup.setBorder(blackLine);
        panelGroup.setSize(300, 1200);

        JLabel name = new JLabel("user : " + name_user);
        JLabel group = new JLabel("Group :");
        Create_group = new JButton("Create a new group");
        Join_group = new JButton("Join a group");


        //position element for group
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panelGroup.add(name, gbc);
        panelGroup.add(group, gbc);
        panelGroup.add(Create_group, gbc);
        panelGroup.add(Join_group, gbc);
        Create_group.addActionListener(new GUI_eventHandlerCreation(gui_gcom));
        Join_group.addActionListener(new GUI_eventHandlerJoin(gui_gcom));

        //---------------------------------- END LEFT PANEL GROUP -------------------------------------//

        //---------------------------------- BEGIN MIDDLE PANEL MESSAGE -------------------------------//

        card = new CardLayout();
        message = new JPanel();
        message.setLayout(card);
        message.setSize(600, 1100);
        message.setPreferredSize(new Dimension(600,600));
        JLabel la = new JLabel("Welcome to our chat app");
        message.add(la);
        message.setName("message welcome");
        panel_group.add(message);

        //---------------------------------- END MIDDLE PANEL MESSAGE ---------------------------------//

        //---------------------------------- BEGIN RIGHT PANEL MEMBERS --------------------------------//

        //Creating panel which include members of the group
        panelMember = new JPanel();
        panelMember.setBorder(blackLine);
        panelMember.setLayout(new GridBagLayout());
        JLabel member = new JLabel("Members");
        gbc_member = new GridBagConstraints();
        gbc_member.gridwidth = GridBagConstraints.REMAINDER;
        panelMember.add(member, gbc_member);

        //---------------------------------- END RIGHT PANEL MEMBER -----------------------------------//

        //---------------------------------- BEGIN BOTTOM PANEL TEXT ----------------------------------//

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JTextField tf = new JTextField(50); // accepts up to 50 characters
        JButton send = new JButton("Send");
        send.addActionListener(new GUI_eventHandlerSend(gui_gcom, tf));
        panel.setSize(600, 100);
        panel.add(tf);
        panel.add(send);

        //---------------------------------- END BOTTOM PANEL TEXT ------------------------------------//

        //Adding Components to the frame.
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.getContentPane().add(panelGroup, BorderLayout.WEST);
        frame.getContentPane().add(panelMember, BorderLayout.EAST);
        frame.getContentPane().add(message, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        this.gui_gcom = gui_gcom;
    }

    //Display groups that are not already displayed
    public void displayGroup(Group group) {
        ArrayList<String> groupGUI = groupsInGUI();
        String UUID = group.getId().toString();
        String name = group.getName();
        if (! groupGUI.contains(UUID)) {
            JButton button_group = new JButton(name);
            button_group.setName(UUID);
            System.out.println(button_group);
            panelGroup.add(button_group, gbc, panelGroup.getComponentCount() - 2);
            CreatePanelForGroup(UUID, name);
            button_group.addActionListener(new GUI_eventHandlerSwitchGroup(gui_gcom, group, panel_group));
            panelGroup.updateUI();
        }
    }

    public ArrayList<String> groupsInGUI() {
        Component[] components = panelGroup.getComponents();
        ArrayList<String> list = new ArrayList<>();
        for (Component component : components) {
            if (!(component.getName() == null)) {
                list.add(component.getName());
            }
        }
        return list;
    }

    public void CreatePanelForGroup(String UUID, String name) {
        if (! IsCreate(UUID)) {
            MessagePanel new_panel_message = new MessagePanel();
            new_panel_message.setSize(600, 1100);
            new_panel_message.setName(UUID);
            new_panel_message.addMessage("Welcome to group : " + name);
            message.add(UUID, new_panel_message);
            panel_group.add(new_panel_message);
            new_panel_message.setVisible(false);
        }
    }

    //Look if a panel with this UUID already exist
    public boolean IsCreate(String UUID) {
        for (JPanel pane : panel_group) {
            if (pane.getName().equals(UUID)) {
                return true;
            }
        }
        return false;
    }

    //Current group displaying
    public void setCurrentGroup(Group group) {
        this.currentGroup = group;
    }

    public void refreshMembersList(Group group) {
        panelMember.removeAll();
        JLabel member = new JLabel("Members");
        JButton leave = new JButton("leave group");
        panelMember.add(member, gbc_member);
        panelMember.add(leave, gbc_member);
        leave.addActionListener(new GUI_eventHandlerLeave(gui_gcom));
        if(currentGroup != null) {
            for(Peer p : group.getMembers()) {
                JLabel peer = new JLabel(p.getName());
                panelMember.add(peer, gbc_member, panelMember.getComponentCount() - 1);
            }
        }
        panelMember.updateUI();
    }

    public void addMember(Peer peer) {
        panelMember.add(new JLabel(peer.getName()), gbc_member, panelMember.getComponentCount() - 1);
        panelMember.updateUI();
    }

    public void removeGroup(Group group) {
        for (Component component : panelGroup.getComponents()) {
            if (component.getName() != null) {
                if (component.getName().equals(group.getId().toString())) {
                    panelGroup.remove(component);
                }
            }
        }
    }

    public void addMessage(Group group, String message) {
        for (JPanel pane : panel_group) {
            if (pane.getName().equals(group.getId().toString())) {
                MessagePanel messagePanel = (MessagePanel) pane;
                messagePanel.addMessage(message);
                return;
            }
        }
    }
}

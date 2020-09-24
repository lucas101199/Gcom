package se.cs.umu.gcom.GUI;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel {

    private DefaultListModel<String> listModel;
    private JList<String> list;

    public MessagePanel() {
        super();
        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(listModel);
        list.setPreferredSize(new Dimension(400,550));
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(500,550));
        this.add(scrollPane);
    }

    public void addMessage(String s) {
        listModel.addElement(s);
    }
}

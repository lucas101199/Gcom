package se.cs.umu.gcom.GUI.debug;

import javafx.beans.property.SimpleStringProperty;

public class ChatHolder {

    private final SimpleStringProperty creator;
    private final SimpleStringProperty content;

    public ChatHolder(String sender, String content) {
        this.creator = new SimpleStringProperty(sender);
        this.content = new SimpleStringProperty(content);
    }

    public String getContent() {
        return content.get();
    }

    public SimpleStringProperty contentProperty() {
        return content;
    }

    public String getCreator() {
        return creator.get();
    }

    public SimpleStringProperty creatorProperty() {
        return creator;
    }
}

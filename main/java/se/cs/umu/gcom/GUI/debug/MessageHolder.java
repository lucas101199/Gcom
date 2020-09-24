package se.cs.umu.gcom.GUI.debug;

import javafx.beans.property.SimpleStringProperty;
import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.ordering.types.VectorClock;

public class MessageHolder {

    private final SimpleStringProperty sender;
    private final SimpleStringProperty creator;
    private final SimpleStringProperty type;
    private final SimpleStringProperty timesReceived;
    private final SimpleStringProperty clock;

    private int count = 1;

    public MessageHolder(Message message, VectorClock clock) {
        this.sender = new SimpleStringProperty(message.getSender().getName());
        this.creator = new SimpleStringProperty(message.getCreator().getName());
        this.type = new SimpleStringProperty(message.toString());
        this.timesReceived = new SimpleStringProperty(count+"");
        this.clock = new SimpleStringProperty(clock == null ? "null" : clock.toString());
    }

    public void received() {
        count++;
        timesReceived.setValue(count+"");
    }

    public String getCreator() {
        return creator.get();
    }

    public SimpleStringProperty creatorProperty() {
        return creator;
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public String getTimesReceived() {
        return timesReceived.get();
    }

    public SimpleStringProperty timesReceivedProperty() {
        return timesReceived;
    }

    public String getClock() {
        return clock.get();
    }

    public SimpleStringProperty clockProperty() {
        return clock;
    }

    public String getSender() {
        return sender.get();
    }

    public SimpleStringProperty senderProperty() {
        return sender;
    }
}

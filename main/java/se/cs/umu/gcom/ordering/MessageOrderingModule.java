package se.cs.umu.gcom.ordering;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.util.HashMap;
import java.util.UUID;

public class MessageOrderingModule {


    private final HashMap<UUID, OrderingMethod> groupOrdering = new HashMap<>();

    public MessageOrderingModule() {}

    public void addGroupOrdering(UUID groupId, OrderingMethod o) {
        groupOrdering.put(groupId,o);
    }

    public void removeGroupOrdering(UUID groupId) {
        groupOrdering.remove(groupId);
    }

    public void orderMessage(VectorClock clock, Message message) {
        OrderingMethod ordering = groupOrdering.get(message.getGroupId());
        if(ordering != null) {
            ordering.orderMessage(clock, message);
        }
    }

    public Message nextMessage(UUID groupId) {
        OrderingMethod ordering = groupOrdering.get(groupId);
        if(ordering != null) {
            return ordering.nextMessage();
        }
        return null;
    }

    public Ordering getOrdering(UUID groupId) {
        return groupOrdering.get(groupId);
    }


}

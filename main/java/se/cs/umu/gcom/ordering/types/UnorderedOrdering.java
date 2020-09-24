package se.cs.umu.gcom.ordering.types;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.misc.Pair;
import se.cs.umu.gcom.ordering.OrderingMethod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class UnorderedOrdering implements OrderingMethod {

    private Deque<Message> holdBackQueue = new ArrayDeque<>();

    @Override
    public void orderMessage(VectorClock clock, Message message) {
        holdBackQueue.push(message);
    }

    @Override
    public Message nextMessage() {
        if(holdBackQueue.isEmpty()) {
            return null;
        }
        return holdBackQueue.pop();
    }

    @Override
    public VectorClock getClock() {
        return null;
    }

    @Override
    public List<Pair<VectorClock, Message>> getQueueContents() {
        return null;
    }

}

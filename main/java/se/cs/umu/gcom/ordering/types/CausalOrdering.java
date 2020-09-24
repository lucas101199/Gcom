package se.cs.umu.gcom.ordering.types;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.misc.Pair;
import se.cs.umu.gcom.ordering.OrderingMethod;

import java.util.*;
import java.util.stream.Collectors;

public class CausalOrdering implements OrderingMethod {

    private final VectorClock clock;
    private Deque<Pair<VectorClock,Message>> holdBackQueue = new ArrayDeque<>();

    public CausalOrdering(Peer owner) {
        this.clock = new VectorClock(owner);
    }

    @Override
    public void orderMessage(VectorClock clock, Message message) {
        holdBackQueue.addLast(new Pair<>(clock,message));
    }

    @Override
    public Message nextMessage() {
        if(holdBackQueue.isEmpty()) {
            return null;
        }
        //Sort based on vector clock - deliverable messages should be placed in the front.
        holdBackQueue = holdBackQueue.stream()
                .sorted(Comparator.comparingInt(p -> clock.compare(p.getKey())))
                .collect(Collectors.toCollection(ArrayDeque::new));


        Pair<VectorClock, Message> pair = holdBackQueue.peek();
        if(pair == null) {
            return null;
        }
        int value = clock.compare(pair.getKey());
        if(value != 0) {
            return null;
        }
        Peer peer = pair.getValue().getCreator();
        clock.tick(peer);

        return holdBackQueue.pop().getValue();
    }

    @Override
    public VectorClock getClock() {
        return clock;
    }

    @Override
    public List<Pair<VectorClock, Message>> getQueueContents() {
        return new ArrayList<>(holdBackQueue);
    }
}

package se.cs.umu.gcom.ordering.types;

import se.cs.umu.gcom.group.Peer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Vector Clock class
 */
public class VectorClock implements Serializable {

    private final Peer owner;

    private HashMap<Peer, Integer> clockValues = new HashMap<>();

    public VectorClock(Peer owner) {
        this.owner = owner;
    }

    /**
     * When positive, their is undelivered previous messages from other.
     * When zero, all previous messages where delivered
     */
    private int checkPreviousMessages(VectorClock other) {
        Integer ourValue = clockValues.getOrDefault(other.owner, -1);
        Integer theirValue = other.clockValues.getOrDefault(other.owner, 0);

        //If never seen other before, we append its value-1 to ourself.
        if (ourValue == -1) {
            clockValues.put(other.owner, theirValue - 1);
            ourValue = theirValue - 1;
        }
        int previous = theirValue - (ourValue + 1);
        if (previous < 0) {
            previous = 0;
        }
        return previous;
    }

    /**
     * Returns the number of undelivered messages that other had delivered
     * at the time of the message.
     */
    private int checkUndeliveredMessages(VectorClock other) {
        int undelivered = 0;

        for (Map.Entry<Peer, Integer> clock1 : other.clockValues.entrySet()) {
            if (clock1.getKey().equals(other.owner)) {
                continue;
            }

            Integer theirValue = clock1.getValue();
            Integer ourValue = clockValues.getOrDefault(clock1.getKey(), -1);

            //If never seen other before, we "append" its value to ourself.
            if (ourValue == -1) {
                clockValues.put(clock1.getKey(), clock1.getValue());
                ourValue = theirValue;
            }

            if (!(theirValue <= ourValue)) {
                undelivered++;
            }

        }


        return undelivered;
    }

    /**
     * Returns the number of undelivered messages this clock has when compared with another.
     * 0 means that it can deliver the message
     * @param other - another VectorClock to compare against.
     * @return
     */
    public int compare(VectorClock other) {
        return checkPreviousMessages(other) + checkUndeliveredMessages(other);
    }

    public void tick(Peer peer) {
        Integer value = clockValues.getOrDefault(peer, 0) + 1;
        clockValues.put(peer, value);
    }

    public void tick() {
        Integer value = clockValues.getOrDefault(owner, 0) + 1;
        clockValues.put(owner, value);
    }

    public void remove(Peer peer) {
        clockValues.remove(peer);
    }

    @Override
    public String toString() {
        return "" + clockValues.entrySet().stream()
                .map(e -> e.getKey().getName() + " - " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

}

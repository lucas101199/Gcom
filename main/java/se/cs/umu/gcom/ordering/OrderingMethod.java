package se.cs.umu.gcom.ordering;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.ordering.types.VectorClock;

/**
 * Represents a method for ordering. Causal/Unordered etc.
 */
public interface OrderingMethod extends Ordering {

    void orderMessage(VectorClock clock, Message message);
    Message nextMessage();
}

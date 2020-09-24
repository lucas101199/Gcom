package se.cs.umu.gcom.ordering;

import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.misc.Pair;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.util.List;

/**
 * Ordering interface, used to expose the clock and the queue contents of a Ordering.
 * Used primarily for debugging in the GcomListener.
 */
public interface Ordering {

    VectorClock getClock();
    List<Pair<VectorClock, Message>> getQueueContents();
}

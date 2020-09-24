package se.cs.umu.gcom.misc;

public class Pair<S,T> {

    private final S key;
    private final T value;

    public Pair(S key, T value) {
        this.key = key;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public S getKey() {
        return key;
    }
}

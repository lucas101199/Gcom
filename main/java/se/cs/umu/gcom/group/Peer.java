package se.cs.umu.gcom.group;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.UUID;

public class Peer implements Serializable {


    private final String name;
    private final UUID uuid;

    private final String ip;

    private Peer(String name, String ip) {
        this.name = name;
        this.ip = ip;
        this.uuid = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public UUID getUUID() {
        return uuid;
    }

    public static Peer createIdentity(String name) throws  UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        return new Peer(name,ip);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return Objects.equals(uuid, peer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return name;
    }
}

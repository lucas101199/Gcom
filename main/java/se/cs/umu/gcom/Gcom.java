package se.cs.umu.gcom;

import se.cs.umu.gcom.communication.*;
import se.cs.umu.gcom.communication.messages.GcomMessage;
import se.cs.umu.gcom.communication.messages.GroupJoinMessage;
import se.cs.umu.gcom.communication.messages.GroupLeftMessage;
import se.cs.umu.gcom.group.events.Event;
import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.GroupManagementModule;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.group.events.MemberRemovedEvent;
import se.cs.umu.gcom.misc.GcomListener;
import se.cs.umu.gcom.misc.RMITools;
import se.cs.umu.gcom.ordering.MessageOrderingModule;
import se.cs.umu.gcom.ordering.Ordering;
import se.cs.umu.gcom.ordering.OrderingMethod;
import se.cs.umu.gcom.ordering.types.CausalOrdering;
import se.cs.umu.gcom.ordering.types.UnorderedOrdering;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Gcom class
 * @param <T> - the class the content you want to communicate with is.
 */
public class Gcom<T extends Serializable> {
    private final CommunicationModule communication;
    private final MessageOrderingModule messageOrdering;
    private final GroupManagementModule groupManagement;

    private final Peer identity;

    private final ExecutorService messageThread = Executors.newSingleThreadExecutor();

    private GcomListener<T> eventListener = null;
    private Registry registry = null;

    private final Supplier<OrderingMethod> ordering;

    public Gcom(String name) throws UnknownHostException {
        this.identity = Peer.createIdentity(name);

        this.ordering = () -> new CausalOrdering(identity);
        this.communication = new ReliableCommunicationModule(identity);

        this.messageOrdering = new MessageOrderingModule();
        this.groupManagement = new GroupManagementModule(identity, this::handleGroupEvent);
    }

    private void handleGroupEvent(Event event) {
        //Someone crashed.
        if(event instanceof MemberRemovedEvent) {
            MemberRemovedEvent e = (MemberRemovedEvent) event;
            Ordering o = messageOrdering.getOrdering(e.getGroup().getId());
            if(o != null && o.getClock() != null) {
                o.getClock().remove(e.getEventPeer());
            }

            if(eventListener != null)
                eventListener.onMemberRemove(e.getGroup(), e.getEventPeer());
        }
    }

    /**
     * This method handles messages before they are delivered up to listeners.
     * Unless they are internal events.
     */
    private void handleMessage(Group g, Message message) {
        if(g == null) {
            return;
        }


        if(message instanceof GroupJoinMessage) {
            //Someone wants to join, add to group.
            GroupJoinMessage m = (GroupJoinMessage) message;
            groupManagement.addMember(g, m.getCreator());

            if(eventListener != null)
                eventListener.onMemberAdded(g, m.getCreator());
        } else if(message instanceof GroupLeftMessage) {
            //Someone left the group, remove them.
            GroupLeftMessage m = (GroupLeftMessage) message;
            groupManagement.removeMember(g, m.getCreator());
            Ordering o = messageOrdering.getOrdering(g.getId());
            if(o != null && o.getClock() != null) {
                o.getClock().remove(m.getCreator());
            }
            if(eventListener != null)
                eventListener.onMemberRemove(g, m.getCreator());
        } else if(message instanceof GcomMessage){
            //Someone sent a message.
            GcomMessage<T> m = (GcomMessage<T>) message;
            if(eventListener != null)
                eventListener.onMemberMessage(g, m.getCreator(), m.getContent());
        }

    }

    public void start() throws RemoteException {
        //Starts receiver and necessary threads
        GcomReceiver receiver = new GcomReceiver() {

            @Override
            public void receive(VectorClock clock, Message message) throws RemoteException {
                //Receiver algorithm.
                messageThread.execute(() -> {
                    // If message doesn't belong to a known group - ignore message.
                    final UUID groupId = message.getGroupId();
                    Group group = groupManagement.getGroup(groupId);
                    if(group == null) {
                        return;
                    }

                    //Communication module receive message
                    boolean passed = communication.receive(group, clock, message);
                    if(!passed) {
                        //Already received message and so we can mostly ignore it.
                        //Even if we ignore it, we receive it again - so send to listener for debugging reasons.
                        if(eventListener != null)
                            eventListener.onMessageReceived(group, message, messageOrdering.getOrdering(groupId));
                        return;
                    }

                    messageOrdering.orderMessage(clock,message);
                    //As long as any messages can be delivered(due to being in right order)
                    //If message is out of order it will simply return null.
                    Message m;
                    while((m = messageOrdering.nextMessage(groupId)) != null) {
                        Group g = groupManagement.getGroup(groupId);
                        handleMessage(g, m);
                        if(eventListener != null)
                            eventListener.onMessageReceived(group, message, messageOrdering.getOrdering(groupId));
                    }
                });

            }

            @Override
            public Group getGroupInfo(UUID groupId) throws RemoteException {
                return groupManagement.getGroup(groupId);
            }

            @Override
            public boolean heartbeat() throws RemoteException {
                return true;
            }
        };


        registry = RMITools.createOrGet(1099);
        if(registry == null) {
            throw new RemoteException();
        }
        registry.rebind(identity.getUUID().toString(), receiver);

        groupManagement.startCrashModule();

    }

    public void stop() throws RemoteException, NotBoundException {
        messageThread.shutdownNow();
        groupManagement.shutdownCrashModule();
        registry.unbind(identity.getUUID().toString());
        registry = null;
    }

    public Peer getSelf() {
        return identity;
    }

    public Group createGroup(String name) {
        Group g = groupManagement.createGroup(name);
        messageOrdering.addGroupOrdering(g.getId(), ordering.get());
        return g;
    }

    public Collection<Group> getJoinedGroups() {
        return groupManagement.getGroups();
    }

    public boolean send(UUID groupId, T content) {
        Group group = groupManagement.getGroup(groupId);
        if(group == null) {
            return false;
        }
        GcomMessage<T> message = new GcomMessage<>(group.getId(), identity, content);
        multicast(group, message);
        if(eventListener != null)
            eventListener.onMemberMessage(group, identity, content);
        return true;
    }

    public boolean joinGroup(UUID groupId, List<Peer> members) {
        for(Peer peer : members) {
            //If join success
            if(joinGroup(groupId, peer.getIp(), peer.getUUID().toString())) {
                return true;
            }
        }
        return false;
    }

    //Note: For cases when we cannot use name server.
    public boolean joinGroup(UUID groupId, String ip, String uuidString) {
        Group g = communication.getGroupInfo(ip, uuidString, groupId);
        if(g == null) {
            return false;
        }
        //Add group, and add yourself to group, create a ordering for the group.
        groupManagement.addGroup(g);
        groupManagement.addMember(g, identity);
        messageOrdering.addGroupOrdering(g.getId(), ordering.get());
        //Send group join message
        multicast(g, new GroupJoinMessage(g.getId(), identity));
        if(eventListener != null)
            eventListener.onGroupJoined(g);
        return true;
    }

    public void leaveGroup(UUID groupId) {
        Group g = groupManagement.getGroup(groupId);
        if(g != null) {
            GroupLeftMessage message = new GroupLeftMessage(groupId, identity);
            Ordering ordering = messageOrdering.getOrdering(message.getGroupId());
            communication.multicast(g.getMembers(), ordering.getClock(), message);
            groupManagement.removeGroup(groupId);
            messageOrdering.removeGroupOrdering(groupId);
            if(eventListener != null)
                eventListener.onMessageSend(g, message, ordering);
        }
    }

    private void multicast(Group group, Message message) {
        Ordering ordering = messageOrdering.getOrdering(message.getGroupId());

        communication.multicast(group.getMembers(), ordering.getClock(), message);
        if(eventListener != null)
            eventListener.onMessageSend(group, message, ordering);
    }

    public void setEventListener(GcomListener<T> listener) {
        this.eventListener = listener;
    }

}


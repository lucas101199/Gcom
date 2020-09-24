package se.cs.umu.gcom.GUI.debug;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import se.cs.umu.gcom.Gcom;
import se.cs.umu.gcom.communication.Message;
import se.cs.umu.gcom.group.Group;
import se.cs.umu.gcom.group.Peer;
import se.cs.umu.gcom.misc.GcomListener;
import se.cs.umu.gcom.misc.Pair;
import se.cs.umu.gcom.nameserver.RemoteNameServer;
import se.cs.umu.gcom.ordering.Ordering;
import se.cs.umu.gcom.ordering.types.VectorClock;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;


public class DebugController implements GcomListener<String> {

    private static final String nameServerIp = "192.168.56.1";

    @FXML
    private TableView<MessageHolder> debugView, queueView;

    @FXML
    private TableView<ChatHolder> chatView;

    @FXML
    private ListView<Peer> memberView, blockedView;

    @FXML
    private Label groupLabel, identityLabel;

    @FXML
    private TextField chatField, joinField;

    @FXML
    private TableColumn<MessageHolder,String> senderColumn,creatorColumn, typeColumn, timesReceivedColumn, clockColumn;

    @FXML
    private TableColumn<MessageHolder,String> queueCreatorCol, queueTypeCol, queueClockCol;

    @FXML
    private TableColumn<ChatHolder, String> chatSenderCol, chatMessageCol;

    private final HashMap<UUID,MessageHolder> debugMap = new HashMap<>();
    private final ObservableList<MessageHolder> debugData = FXCollections.observableArrayList();

    private final ObservableList<MessageHolder> queueData = FXCollections.observableArrayList();
    private final ObservableList<ChatHolder> chatData = FXCollections.observableArrayList();

    private final ObservableList<Peer> blockedPeers = FXCollections.observableArrayList();


    //Gcom middleware content.
    private Gcom<String> gcom;
    private Group joinedGroup = null;

    @FXML
    private void initialize(){
        blockedView.setItems(blockedPeers);

        //Debug view
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        creatorColumn.setCellValueFactory(new PropertyValueFactory<>("creator"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        timesReceivedColumn.setCellValueFactory(new PropertyValueFactory<>("timesReceived"));
        clockColumn.setCellValueFactory(new PropertyValueFactory<>("clock"));
        debugView.setItems(debugData);

        //Queue view
        queueCreatorCol.setCellValueFactory(new PropertyValueFactory<>("creator"));
        queueTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        queueClockCol.setCellValueFactory(new PropertyValueFactory<>("clock"));
        queueView.setItems(queueData);

        //Chat View
        chatSenderCol.setCellValueFactory(new PropertyValueFactory<>("creator"));
        chatMessageCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        chatView.setItems(chatData);

        chatField.setOnAction(event -> {
            if(joinedGroup == null) {
                return;
            }

            String content = chatField.getText();
            if(!content.isEmpty()) {
                chatField.setText("");

                block();
                gcom.send(joinedGroup.getId(),content);
                unblock();
            }
        });

        final String defaultName = "Tester" + new Random().nextInt(1000);

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setHeaderText("Enter nickname");
        Optional<String> str = nameDialog.showAndWait();

        final String name = str.orElse(defaultName);
        System.out.println(name);
        identityLabel.setText("Identity: " + name);

        //Gcom
        try {

            gcom = new Gcom<>(name);
            gcom.setEventListener(this);
            gcom.start();
        } catch (UnknownHostException | RemoteException e) {
            e.printStackTrace();
            Platform.exit();
        }

    }

    public List<Group> getNameServerGroups() {
        try {
            Registry registry = LocateRegistry.getRegistry(nameServerIp);
            RemoteNameServer stub = (RemoteNameServer)registry.lookup("NameServer");
            return stub.getGroups();
        } catch (RemoteException | NotBoundException e) {
            return null;
        }
    }

    private void resetViews() {
        groupLabel.setText("Group: None");
        debugMap.clear();
        debugData.clear();
        chatData.clear();
        queueData.clear();
        queueView.refresh();
        blockedPeers.clear();
        memberView.setItems(FXCollections.emptyObservableList());
    }


    @FXML
    private void onBlockButton() {
        if(joinedGroup != null && !memberView.getSelectionModel().isEmpty()) {
            Peer p = memberView.getSelectionModel().getSelectedItem();
            if(p != null && !p.equals(gcom.getSelf()) && !blockedPeers.contains(p)) {
                blockedPeers.add(p);
            }
        }
    }

    @FXML
    private void onUnblockButton() {
        if(joinedGroup != null && !blockedView.getSelectionModel().isEmpty()) {
            Peer p = blockedView.getSelectionModel().getSelectedItem();
            if(p != null) {
                blockedPeers.remove(p);
            }
        }
    }

    @FXML
    private void onLeaveButton() {
        if(joinedGroup != null) {
            Group g = joinedGroup;
            joinedGroup = null;

            block();
            gcom.leaveGroup(g.getId());
            unblock();

            resetViews();
        }
    }

    @FXML
    private void onJoinButton() {
        if(joinedGroup != null) {
            return;
        }
        String groupName = joinField.getText();

        List<Group> groups = getNameServerGroups();
        if(groups != null) {
            Optional<Group> optionalGroup = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();
            if(optionalGroup.isPresent()) {

                Group g = optionalGroup.get();
                gcom.joinGroup(g.getId(),g.getMembers());
            }

        }
    }

    @FXML
    private void onCreateButton() {
        if(joinedGroup != null) {
            return;
        }
        String groupName = joinField.getText();
        joinedGroup = gcom.createGroup(groupName);
        try {
            Registry registry = LocateRegistry.getRegistry(nameServerIp, 1099);
            RemoteNameServer stub = (RemoteNameServer)registry.lookup("NameServer");
            stub.addGroup(joinedGroup);
        } catch (RemoteException | NotBoundException ignored) {}

        updateMemberList(joinedGroup);
        Platform.runLater(() -> groupLabel.setText("Group: " + joinedGroup.getName()));
    }

    private void updateMemberList(Group group) {
        ObservableList<Peer> groupMembers = FXCollections.observableList(group.getMembers());
        Platform.runLater(() -> memberView.setItems(groupMembers));
    }

    private void updateHoldBackQueue(Ordering ordering) {
        if(ordering == null) {
            return;
        }
        List<Pair<VectorClock,Message>> queueContents = ordering.getQueueContents();
        if(queueContents == null) {
            return;
        }
        List<MessageHolder> list = queueContents.stream().map(pair -> new MessageHolder(pair.getValue(),pair.getKey())).collect(Collectors.toList());

        Platform.runLater(() -> {
            queueData.clear();
            queueData.addAll(list);
        });
    }

    @Override
    public void onGroupJoined(Group group) {
        joinedGroup = group;
        updateMemberList(joinedGroup);
        Platform.runLater(() -> groupLabel.setText("Group: " + group.getName()));
    }

    @Override
    public void onMemberAdded(Group group, Peer peer) {
        if(joinedGroup == null) {
            return;
        }
        Platform.runLater(() -> {
            updateMemberList(joinedGroup);
            chatData.add(new ChatHolder("System",peer.getName() + " has joined."));
        });
    }

    @Override
    public void onMemberRemove(Group group, Peer peer) {
        if(joinedGroup == null) {
            return;
        }
        Platform.runLater(() -> {
            updateMemberList(joinedGroup);
            chatData.add(new ChatHolder("System",peer.getName() + " has left."));
        });
    }

    @Override
    public void onMemberMessage(Group group, Peer sender, String content) {

        Platform.runLater(() -> chatData.add(new ChatHolder(sender.getName(),content)));
    }

    @Override
    public void onMessageReceived(Group group, Message message, Ordering ordering) {
        if(debugMap.containsKey(message.getMessageId())) {
            MessageHolder holder = debugMap.get(message.getMessageId());
            Platform.runLater(holder::received);
            return;
        }
        VectorClock clock = ordering == null ? null : ordering.getClock();
        MessageHolder holder = new MessageHolder(message,clock);
        debugMap.put(message.getMessageId(),holder);

        Platform.runLater(() -> {
            if(joinedGroup == null) {
                return;
            }
            debugData.add(holder);
        });

        updateHoldBackQueue(ordering);
    }

    @Override
    public void onMessageSend(Group group, Message message, Ordering ordering) {
        updateHoldBackQueue(ordering);
    }

    private void block() {
        if(joinedGroup == null) {
            return;
        }
        for(Peer p : blockedPeers) {
            joinedGroup.getMembers().remove(p);
        }
    }

    private void unblock() {
        if(joinedGroup == null) {
            return;
        }
        for(Peer p : blockedPeers) {
            joinedGroup.getMembers().add(p);
        }
    }

}

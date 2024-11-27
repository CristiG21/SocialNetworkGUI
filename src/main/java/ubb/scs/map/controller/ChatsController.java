package ubb.scs.map.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.SocialNetworkApplication;
import ubb.scs.map.domain.Chat;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.ChatService;
import ubb.scs.map.service.MessageService;
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.ChatEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class ChatsController implements Observer<ChatEntityChangeEvent> {
    Long userId;
    private UtilizatorService utilizatorService;
    private ChatService chatService;
    private MessageService messageService;
    private final ObservableList<Chat> model = FXCollections.observableArrayList();

    @FXML
    private ListView<Chat> listView;

    public void setService(UtilizatorService utilizatorService, ChatService chatService, MessageService messageService, Long userId) {
        this.utilizatorService = utilizatorService;
        this.chatService = chatService;
        this.messageService = messageService;
        this.userId = userId;
        chatService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Chat chat, boolean empty) {
                super.updateItem(chat, empty);
                if (empty || chat == null) {
                    setText(null);
                } else {
                    setText(chat.getName());
                }
            }
        });
        listView.setItems(model);
    }

    private void initModel() {
        Iterable<Chat> conversatii = chatService.getAllChatsByUserId(userId);
        List<Chat> chats = StreamSupport.stream(conversatii.spliterator(), false)
                .toList();
        model.setAll(chats);
    }

    @Override
    public void update(ChatEntityChangeEvent chatEntityChangeEvent) {
        initModel();
    }

    @FXML
    public void handleAddChat(ActionEvent actionEvent) {
        showEditChatDialog(null);
    }

    @FXML
    public void handleModifyChat(ActionEvent actionEvent) {
        Chat chat = listView.getSelectionModel().getSelectedItem();
        if (chat == null) {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun chat!");
            return;
        }
        if (!Objects.equals(chat.getAdminId(), userId)) {
            MessageAlert.showErrorMessage(null, "Nu sunteti administrator al chat-ului!");
            return;
        }

        showEditChatDialog(chat);
    }

    @FXML
    public void handleDeleteChat(ActionEvent actionEvent) {
        Chat chat = listView.getSelectionModel().getSelectedItem();
        if (chat != null) {
            try {
                chatService.removeChat(chat.getId(), userId);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Chat-ul a fost sters cu succes!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun chat!");
        }
    }

    @FXML
    public void handleAddUtilizatoriChat(ActionEvent actionEvent) {
        Chat chat = listView.getSelectionModel().getSelectedItem();
        if (chat == null) {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun chat!");
            return;
        }
        if (!Objects.equals(chat.getAdminId(), userId)) {
            MessageAlert.showErrorMessage(null, "Nu sunteti administrator al acestui chat!");
            return;
        }

        showAddUtilizatoriChatDialog(chat);
    }

    @FXML
    public void handleOpenChat(ActionEvent actionEvent) {
        Chat chat = listView.getSelectionModel().getSelectedItem();
        if (chat != null)
            showChatDialog(chat);
        else {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun chat!");
        }
    }

    public void showEditChatDialog(Chat chat) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/edit-chat-view.fxml"));
            AnchorPane root = fxmlLoader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(chat == null ? "Add Chat" : "Edit Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            EditChatController editChatController = fxmlLoader.getController();
            editChatController.setService(chatService, dialogStage, chat, userId);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddUtilizatoriChatDialog(Chat chat) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/add-utilizatori-chat-view.fxml"));
            AnchorPane root = fxmlLoader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Utilizatori Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            AddUtilizatoriChatController addUtilizatoriChatController = fxmlLoader.getController();
            addUtilizatoriChatController.setService(utilizatorService, chatService, chat.getId());

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showChatDialog(Chat chat) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/chat-view.fxml"));
            AnchorPane root = fxmlLoader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chat - " + chat.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            ChatController chatController = fxmlLoader.getController();
            chatController.setService(messageService, chat.getId(), userId);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

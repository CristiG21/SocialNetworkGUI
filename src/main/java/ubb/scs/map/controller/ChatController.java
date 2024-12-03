package ubb.scs.map.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ubb.scs.map.dto.MessageDto;
import ubb.scs.map.domain.validators.ValidationException;
import ubb.scs.map.service.MessageService;
import ubb.scs.map.utils.events.MessageEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;

import java.util.List;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<MessageEntityChangeEvent> {
    private Long chatId;
    private Long userId;
    private MessageService messageService;
    private final ObservableList<MessageDto> model = FXCollections.observableArrayList();

    @FXML
    private ListView<MessageDto> listView;
    @FXML
    private TextField textField;

    public void setService(MessageService messageService, Long chatId, Long userId) {
        this.messageService = messageService;
        this.chatId = chatId;
        this.userId = userId;
        messageService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessageDto messageDto, boolean empty) {
                super.updateItem(messageDto, empty);
                if (empty || messageDto == null) {
                    setText(null);
                } else {
                    if(messageDto.getReplyMessage() != null)
                        setText(messageDto.getUserName() + ": " + messageDto.getMessage() + " - Reply To: " + messageDto.getReplyMessage());
                    else
                        setText(messageDto.getUserName() + ": " + messageDto.getMessage());
                }
            }
        });
        listView.setItems(model);
    }

    private void initModel() {
        Iterable<MessageDto> mesaje = messageService.getAllMessagesByChatId(chatId);
        List<MessageDto> messages = StreamSupport.stream(mesaje.spliterator(), false)
                .toList();
        model.setAll(messages);
    }

    @Override
    public void update(MessageEntityChangeEvent messageEntityChangeEvent) {
        initModel();
    }

    @FXML
    public void handleSendMessage(ActionEvent actionEvent) {
        sendMessage(null);
    }

    @FXML
    public void handleReplyMessage(ActionEvent actionEvent) {
        MessageDto message = listView.getSelectionModel().getSelectedItem();
        if (message != null)
            sendMessage(message.getId());
        else
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun mesaj!");
    }

    private void sendMessage(Long replyMessageId) {
        try {
            messageService.addMessage(userId, chatId, textField.getText(), replyMessageId);
            textField.setText("");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}

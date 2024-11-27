package ubb.scs.map.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ubb.scs.map.domain.Chat;
import ubb.scs.map.domain.validators.ValidationException;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.ChatService;

public class EditChatController {
    @FXML
    private TextField textFieldName;

    private ChatService chatService;
    private Stage dialogStage;
    private Chat chat;
    private Long userId;

    @FXML
    private void initialize() {
    }

    public void setService(ChatService chatService, Stage stage, Chat chat, Long userId) {
        this.chatService = chatService;
        this.dialogStage = stage;
        this.chat = chat;
        this.userId = userId;
        if (chat != null)
            setFields(chat);
    }

    @FXML
    public void handleSave() {
        String name = textFieldName.getText();
        Long adminId = userId;

        if (chat == null) {
            saveChat(name, adminId);
        } else {
            updateChat(chat.getId(), name, adminId);
        }
    }

    private void saveChat(String name, Long adminId) {
        try {
            chatService.addChat(name, adminId);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Chat-ul a fost salvat cu succes!");
            dialogStage.close();
        } catch (ValidationException | ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void updateChat(Long id, String name, Long adminId) {
        try {
            chatService.modifyChat(id, name, adminId);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Chat-ul a fost modificat cu succes!");
            dialogStage.close();
        } catch (ValidationException | ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void setFields(Chat chat) {
        textFieldName.setText(chat.getName());
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }
}

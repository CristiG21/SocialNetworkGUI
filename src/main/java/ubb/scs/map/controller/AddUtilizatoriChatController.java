package ubb.scs.map.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.service.ChatService;
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddUtilizatoriChatController implements Observer<UtilizatorEntityChangeEvent> {
    private Long chatId;
    private UtilizatorService utilizatorService;
    private ChatService chatService;
    private final ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    private TableView<Utilizator> tableView;
    @FXML
    private TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    private TableColumn<Utilizator, String> tableColumnLastName;

    public void setService(UtilizatorService utilizatorService, ChatService chatService, Long chatId) {
        this.utilizatorService = utilizatorService;
        this.chatService = chatService;
        this.chatId = chatId;
        utilizatorService.addObserver(this);
        initModel();
    }

    @FXML
    private void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initModel() {
        Iterable<Utilizator> utilizatori = utilizatorService.getAllUtilizatori();
        List<Utilizator> users = StreamSupport.stream(utilizatori.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(UtilizatorEntityChangeEvent utilizatorEntityChangeEvent) {
        initModel();
    }

    @FXML
    public void handleAddUtilizatoriChat(ActionEvent actionEvent) {
        ObservableList<Utilizator> utilizatori = tableView.getSelectionModel().getSelectedItems();
        if (!utilizatori.isEmpty()) {
            chatService.addUtilizatoriChat(utilizatori, chatId);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Utilizatorii au fost adaugati cu succes la chat!");
        } else {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun utilizator!");
        }
    }
}

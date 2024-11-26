package ubb.scs.map.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.SocialNetworkApplication;
import ubb.scs.map.domain.PrietenieDto;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.PrietenieService;
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.PrietenieEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

public class PrieteniiController implements Observer<PrietenieEntityChangeEvent> {
    Long userId;
    private UtilizatorService utilizatorService;
    private PrietenieService prietenieService;
    private final ObservableList<PrietenieDto> modelReceived = FXCollections.observableArrayList();
    private final ObservableList<PrietenieDto> modelSent = FXCollections.observableArrayList();

    @FXML
    private TableView<PrietenieDto> tableViewReceived;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnReceivedFirstName;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnReceivedLastName;
    @FXML
    private TableColumn<PrietenieDto, LocalDateTime> tableColumnReceivedFriendsFrom;
    @FXML
    private TableColumn<PrietenieDto, Boolean> tableColumnReceivedAccepted;
    @FXML
    private TableView<PrietenieDto> tableViewSent;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnSentFirstName;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnSentLastName;
    @FXML
    private TableColumn<PrietenieDto, LocalDateTime> tableColumnSentFriendsFrom;
    @FXML
    private TableColumn<PrietenieDto, Boolean> tableColumnSentAccepted;


    public void setService(UtilizatorService utilizatorService, PrietenieService prietenieService, Long userId) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.userId = userId;
        prietenieService.addObserver(this);
        initModel();

        long pendingFriendships = modelReceived.stream()
                .filter(prietenieDto -> !prietenieDto.getAccepted())
                .count();
        if (pendingFriendships > 0)
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Atentie!", "Aveti cereri noi de prietenie!");
    }

    @FXML
    public void initialize() {
        tableColumnReceivedFirstName.setCellValueFactory(new PropertyValueFactory<>("utilizatorFirstName"));
        tableColumnReceivedLastName.setCellValueFactory(new PropertyValueFactory<>("utilizatorLastName"));
        tableColumnReceivedFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
        tableColumnReceivedAccepted.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        tableViewReceived.setItems(modelReceived);

        tableColumnSentFirstName.setCellValueFactory(new PropertyValueFactory<>("utilizatorFirstName"));
        tableColumnSentLastName.setCellValueFactory(new PropertyValueFactory<>("utilizatorLastName"));
        tableColumnSentFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
        tableColumnSentAccepted.setCellValueFactory(new PropertyValueFactory<>("accepted"));
        tableViewSent.setItems(modelSent);
    }

    private void initModel() {
        Iterable<PrietenieDto> prieteniiReceived = prietenieService.getAllReceivedPrietenii(userId);
        List<PrietenieDto> friendshipsReceived = StreamSupport.stream(prieteniiReceived.spliterator(), false)
                .toList();
        modelReceived.setAll(friendshipsReceived);

        Iterable<PrietenieDto> prieteniiSent = prietenieService.getAllSentPrietenii(userId);
        List<PrietenieDto> friendshipsSent = StreamSupport.stream(prieteniiSent.spliterator(), false)
                .toList();
        modelSent.setAll(friendshipsSent);
    }

    @Override
    public void update(PrietenieEntityChangeEvent utilizatorEntityChangeEvent) {
        initModel();
    }

    @FXML
    public void handleAddPrietenie(ActionEvent actionEvent) {
        showAddPrietenieDialog();
    }

    @FXML
    public void handleDeleteReceivedPrietenie(ActionEvent actionEvent) {
        PrietenieDto prietenie = tableViewReceived.getSelectionModel().getSelectedItem();
        handleDeletePrietenie(prietenie);
    }

    @FXML
    public void handleDeleteSentPrietenie(ActionEvent actionEvent) {
        PrietenieDto prietenie = tableViewSent.getSelectionModel().getSelectedItem();
        handleDeletePrietenie(prietenie);
    }

    private void handleDeletePrietenie(PrietenieDto prietenie) {
        if (prietenie != null) {
            try {
                prietenieService.removePrietenie(prietenie.getId());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Prietenia a fost stearsa cu succes!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Nu este selectata nicio prietenie!");
        }
    }

    @FXML
    public void handleAcceptPrietenie(ActionEvent actionEvent) {
        PrietenieDto prietenie = tableViewReceived.getSelectionModel().getSelectedItem();
        if (prietenie == null) {
            MessageAlert.showErrorMessage(null, "Nu este selectata nicio prietenie!");
            return;
        }

        try {
            prietenieService.acceptPrietenie(userId, prietenie.getId());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Prietenia a fost acceptata cu succes!");
        } catch (ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    public void showAddPrietenieDialog() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/add-prietenie-view.fxml"));
            AnchorPane root = fxmlLoader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Prietenie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            AddPrietenieController addPrietenieViewController = fxmlLoader.getController();
            addPrietenieViewController.setService(utilizatorService, prietenieService, userId);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

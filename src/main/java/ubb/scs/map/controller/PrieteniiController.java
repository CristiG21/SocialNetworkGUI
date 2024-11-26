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
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrieteniiController implements Observer<UtilizatorEntityChangeEvent> {
    Long userId;
    private UtilizatorService utilizatorService;
    private ObservableList<PrietenieDto> model = FXCollections.observableArrayList();

    @FXML
    private TableView<PrietenieDto> tableView;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnSenderFirstName;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnSenderLastName;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnReceiverFirstName;
    @FXML
    private TableColumn<PrietenieDto, String> tableColumnReceiverLastName;
    @FXML
    private TableColumn<PrietenieDto, LocalDateTime> tableColumnFriendsFrom;
    @FXML
    private TableColumn<PrietenieDto, Boolean> tableColumnAccepted;


    public void setService(UtilizatorService utilizatorService, Long userId) {
        this.utilizatorService = utilizatorService;
        this.userId = userId;
        utilizatorService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnSenderFirstName.setCellValueFactory(new PropertyValueFactory<PrietenieDto, String>("utilizator1FirstName"));
        tableColumnSenderLastName.setCellValueFactory(new PropertyValueFactory<PrietenieDto, String>("utilizator1LastName"));
        tableColumnReceiverFirstName.setCellValueFactory(new PropertyValueFactory<PrietenieDto, String>("utilizator2FirstName"));
        tableColumnReceiverLastName.setCellValueFactory(new PropertyValueFactory<PrietenieDto, String>("utilizator2LastName"));
        tableColumnFriendsFrom.setCellValueFactory(new PropertyValueFactory<PrietenieDto, LocalDateTime>("friendsFrom"));
        tableColumnAccepted.setCellValueFactory(new PropertyValueFactory<PrietenieDto, Boolean>("accepted"));

        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<PrietenieDto> prietenii = utilizatorService.getAllPrieteniiByUtilizator(userId);
        List<PrietenieDto> friendships = StreamSupport.stream(prietenii.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(friendships);
    }

    @Override
    public void update(UtilizatorEntityChangeEvent utilizatorEntityChangeEvent) {
        initModel();
    }

    @FXML
    public void handleAddPrietenie(ActionEvent actionEvent) {
        showAddPrietenieDialog();
    }

    @FXML
    public void handleDeletePrietenie(ActionEvent actionEvent) {
        PrietenieDto prietenie = (PrietenieDto) tableView.getSelectionModel().getSelectedItem();
        if (prietenie != null) {
            try {
                utilizatorService.removePrietenie(prietenie.getId());
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
        PrietenieDto prietenie = (PrietenieDto) tableView.getSelectionModel().getSelectedItem();
        if (prietenie == null) {
            MessageAlert.showErrorMessage(null, "Nu este selectata nicio prietenie!");
            return;
        }

        try {
            utilizatorService.acceptPrietenie(userId, prietenie.getId());
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
            addPrietenieViewController.setService(utilizatorService, userId);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

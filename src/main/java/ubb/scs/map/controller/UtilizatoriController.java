package ubb.scs.map.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.SocialNetworkApplication;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatoriController implements Observer<UtilizatorEntityChangeEvent> {
    private UtilizatorService utilizatorService;
    private ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    private TableView<Utilizator> tableView;
    @FXML
    private TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    private TableColumn<Utilizator, String> tableColumnLastName;


    public void setService(UtilizatorService utilizatorService) {
        this.utilizatorService = utilizatorService;
        utilizatorService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<Utilizator> messages = utilizatorService.getAllUtilizatori();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(UtilizatorEntityChangeEvent utilizatorEntityChangeEvent) {
        initModel();
    }

    @FXML
    public void handleAddUtilizator(ActionEvent actionEvent) {
        showEditUtilizatorDialog(null);
    }

    @FXML
    public void handleDeleteUtilizator(ActionEvent actionEvent) {
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (utilizator != null) {
            try {
                utilizatorService.removeUtilizator(utilizator.getId());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Utilizatorul a fost sters cu succes!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun utilizator!");
        }
    }

    @FXML
    public void handleUpdateUtilizator(ActionEvent actionEvent) {
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (utilizator != null)
            showEditUtilizatorDialog(utilizator);
        else
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun utilizator!");
    }

    @FXML
    public void handleOpenPrietenii(ActionEvent actionEvent) {
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (utilizator == null) {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun utilizator!");
            return;
        }

        try {
            // create a new stage for the popup dialog.
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/prietenii-view.fxml"));
            AnchorPane root = fxmlLoader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Friendships");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            PrieteniiController prieteniiController = fxmlLoader.getController();
            prieteniiController.setService(utilizatorService, utilizator.getId());

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEditUtilizatorDialog(Utilizator utilizator) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("views/edit-utilizator-view.fxml"));
            AnchorPane root = fxmlLoader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(utilizator == null ? "Add Utilizator" : "Edit Utilizator");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));

            EditUtilizatorController editMessageViewController = fxmlLoader.getController();
            editMessageViewController.setService(utilizatorService, dialogStage, utilizator);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

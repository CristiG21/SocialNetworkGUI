package ubb.scs.map.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddPrietenieController implements Observer<UtilizatorEntityChangeEvent> {
    private Long userId;
    private UtilizatorService utilizatorService;
    private ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    private TableView<Utilizator> tableView;
    @FXML
    private TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    private TableColumn<Utilizator, String> tableColumnLastName;

    public void setService(UtilizatorService utilizatorService, Long userId) {
        this.utilizatorService = utilizatorService;
        this.userId = userId;
        initModel();
    }

    @FXML
    private void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);
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
    public void handleAddPrietenie(ActionEvent actionEvent) {
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (utilizator != null) {
            try {
                utilizatorService.addPrietenie(userId, utilizator.getId());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Prietenia a fost adaugata cu succes!");
            } catch (ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Nu este selectat niciun utilizator!");
        }
    }
}

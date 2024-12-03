package ubb.scs.map.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.SocialNetworkApplication;
import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.dto.PrietenieDto;
import ubb.scs.map.dto.PrietenieFilterDto;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.PrietenieService;
import ubb.scs.map.service.UtilizatorService;
import ubb.scs.map.utils.events.PrietenieEntityChangeEvent;
import ubb.scs.map.utils.observer.Observer;
import ubb.scs.map.utils.paging.Page;
import ubb.scs.map.utils.paging.Pageable;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private Button buttonPreviousPageReceived;
    @FXML
    private Button buttonNextPageReceived;
    @FXML
    private Label labelPageReceived;
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
    @FXML
    private Button buttonPreviousPageSent;
    @FXML
    private Button buttonNextPageSent;
    @FXML
    private Label labelPageSent;

    private int pageSize = 2;
    private int currentPageReceived = 0;
    private int currentPageSent = 0;
    private int totalNumberOfElementsReceived = 0;
    private int totalNumberOfElementsSent = 0;

    private PrietenieFilterDto filter = new PrietenieFilterDto();

    public void setService(UtilizatorService utilizatorService, PrietenieService prietenieService, Long userId) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.userId = userId;
        prietenieService.addObserver(this);
        filter.setUserId(Optional.of(userId));
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
        filter.setReceived(Optional.of(true));
        Page<PrietenieDto> page = prietenieService.findAllOnPage(new Pageable(currentPageReceived, pageSize), filter);
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPageReceived > maxPage) {
            currentPageReceived = maxPage;
            page = prietenieService.findAllOnPage(new Pageable(currentPageReceived, pageSize), filter);
        }
        totalNumberOfElementsReceived = page.getTotalNumberOfElements();
        buttonPreviousPageReceived.setDisable(currentPageReceived == 0);
        buttonNextPageReceived.setDisable(currentPageReceived == maxPage);
        List<PrietenieDto> friendshipsReceived = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .toList();
        modelReceived.setAll(friendshipsReceived);
        labelPageReceived.setText("Page " + (currentPageReceived + 1) + " of " + (maxPage + 1));

        filter.setReceived(Optional.of(false));
        page = prietenieService.findAllOnPage(new Pageable(currentPageSent, pageSize), filter);
        maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPageSent > maxPage) {
            currentPageSent = maxPage;
            page = prietenieService.findAllOnPage(new Pageable(currentPageSent, pageSize), filter);
        }
        totalNumberOfElementsSent = page.getTotalNumberOfElements();
        buttonPreviousPageSent.setDisable(currentPageSent == 0);
        buttonNextPageSent.setDisable(currentPageSent== maxPage);
        List<PrietenieDto> friendshipsSent = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .toList();
        modelSent.setAll(friendshipsSent);
        labelPageSent.setText("Page " + (currentPageSent + 1) + " of " + (maxPage + 1));
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

    public void handlePreviousPageReceived(ActionEvent actionEvent) {
        currentPageReceived--;
        initModel();
    }

    public void handleNextPageReceived(ActionEvent actionEvent) {
        currentPageReceived++;
        initModel();
    }

    public void handlePreviousPageSent(ActionEvent actionEvent) {
        currentPageSent--;
        initModel();
    }

    public void handleNextPageSent(ActionEvent actionEvent) {
        currentPageSent++;
        initModel();
    }
}

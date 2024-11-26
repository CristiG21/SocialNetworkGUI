package ubb.scs.map.controller;

import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.domain.validators.ValidationException;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.service.UtilizatorService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUtilizatorController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;

    private UtilizatorService utilizatorService;
    private Stage dialogStage;
    private Utilizator utilizator;

    @FXML
    private void initialize() {
    }

    public void setService(UtilizatorService utilizatorService, Stage stage, Utilizator u) {
        this.utilizatorService = utilizatorService;
        this.dialogStage = stage;
        this.utilizator = u;
        if (u != null)
            setFields(u);
    }

    @FXML
    public void handleSave() {
        String firstNameText = textFieldFirstName.getText();
        String lastNameText = textFieldLastName.getText();

        if (utilizator == null) {
            saveUtilizator(firstNameText, lastNameText);
        } else {
            Long id = Long.parseLong(textFieldId.getText());
            updateUtilizator(id, firstNameText, lastNameText);
        }
    }

    private void updateUtilizator(Long id, String firstName, String lastName) {
        try {
            utilizatorService.modifyUtilizator(id, firstName, lastName);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Utilizatorul a fost modificat cu succes!");
            dialogStage.close();
        } catch (ValidationException | ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void saveUtilizator(String firstName, String lastName) {
        try {
            utilizatorService.addUtilizator(firstName, lastName);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Utilizatorul a fost salvat cu succes!");
            dialogStage.close();
        } catch (ValidationException | ServiceException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void setFields(Utilizator u) {
        textFieldId.setText(u.getId().toString());
        textFieldFirstName.setText(u.getFirstName());
        textFieldLastName.setText(u.getLastName());
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }
}

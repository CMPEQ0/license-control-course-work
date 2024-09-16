package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.SecretDao;
import com.cmpeq0.controlsystem.entity.LicenceKey;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelRoomsReadyEvent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelSecretsReadyEvent;
import com.cmpeq0.controlsystem.utils.SecretKeyManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UpdateLicenseController {

    private final UiStateComponent uiStateComponent;
    private final SecretDao secretDao;
    private final ApplicationContext applicationContext;

    public enum Mode {
        NEW,
        UPDATE
    }

    private Mode mode = Mode.NEW;

    @FXML
    public Label errorLabel;

    @FXML
    public Label idLabel;

    @FXML
    public TextField nameField;

    @FXML
    public PasswordField keyField;

    @FXML
    public TreeTableView<Worker> table;

    @FXML
    public Button saveButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button bufferButton;

    private void printError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void printError() {
        printError("");
    }

    private void printTable() {
        var columns = table.getColumns();
        columns.get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));

        var root = new TreeItem<Worker>();
        for (var worker : uiStateComponent.getSecret().getWorkers()) {
            TreeItem<Worker> item = new TreeItem<>(worker);
            root.getChildren().add(item);
        }
        root.setExpanded(true);
        table.setShowRoot(false);
        table.setRoot(root);
    }

    private String getLicenseKey() {
        String secretKey = uiStateComponent.getSecret().getSecretKey();
        String encrypted = uiStateComponent.getSecret().getLicence().getKey();
        return SecretKeyManager.decrypt(encrypted, secretKey);
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: red;");

        if (uiStateComponent.getSecret() != null) {
            mode = Mode.UPDATE;
        } else {
            mode = Mode.NEW;
        }

        if (mode != Mode.NEW) {
            idLabel.setText(Long.toString(uiStateComponent.getSecret().getId()));
            nameField.setText(uiStateComponent.getSecret().getName());
            keyField.setText(getLicenseKey());
            //printing secrets
            printTable();
        }

        saveButton.setOnAction(actionEvent -> {
            if (nameField.getText().isEmpty()) {
                printError("Заполните поле наименования");
            } else if (keyField.getText().isEmpty()) {
                printError("Заполните поле ключа");
            } else {
                if (mode == Mode.NEW) {
                    LicenceKey licence = LicenceKey.builder()
                            .name(nameField.getText())
                            .key(keyField.getText()).build();


                    var secret = secretDao.createLicenceKey(licence);
                    secretDao.addSecretToWorker(uiStateComponent.getActiveUser().getId(), secret.getId());
                } else {
                    LicenceKey licence = LicenceKey.builder()
                            .id(uiStateComponent.getSecret().getLicence().getId())
                            .name(nameField.getText())
                            .key(keyField.getText())
                            .secret(uiStateComponent.getSecret()).build();

                    secretDao.updateLicence(licence);
                }

                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new ControlPanelSecretsReadyEvent(uiStateComponent.getStage()));
                if (old != null) {
                    old.hide();
                }
            }
        });

        deleteButton.setOnAction(actionEvent -> {
            if (mode == Mode.UPDATE) {
                secretDao.removeLicence(uiStateComponent.getSecret().getId());
            }
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelSecretsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });

        bufferButton.setOnAction(actionEvent -> {
            String key = keyField.getText();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(key);
            clipboard.setContent(content);
        });
    }

}

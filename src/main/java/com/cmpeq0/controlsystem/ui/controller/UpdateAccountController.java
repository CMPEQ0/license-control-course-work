package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.SecretDao;
import com.cmpeq0.controlsystem.entity.Account;
import com.cmpeq0.controlsystem.entity.LicenceKey;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
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

@Component
@RequiredArgsConstructor
public class UpdateAccountController {

    private final UiStateComponent uiStateComponent;
    private final ApplicationContext applicationContext;
    private final SecretDao secretDao;

    public enum Mode {
        NEW,
        UPDATE
    }

    private Mode mode;

    @FXML
    public Label idLabel;

    @FXML
    public TextField nameField;

    @FXML
    public PasswordField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public TreeTableView<Worker> table;

    @FXML
    public Label errorLabel;

    @FXML
    public Button deleteButton;

    @FXML
    public Button saveButton;

    @FXML
    public Button loginBufferButton;

    @FXML
    public Button passwordBufferButton;

    private String decryptField(String encrypted) {
        String secretKey = uiStateComponent.getSecret().getSecretKey();
        return SecretKeyManager.decrypt(encrypted, secretKey);
    }

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
            loginField.setText(decryptField(uiStateComponent.getSecret().getAccount().getLogin()));
            passwordField.setText(decryptField(uiStateComponent.getSecret().getAccount().getPassword()));
            //printing secrets
            printTable();
        }

        saveButton.setOnAction(actionEvent -> {
            if (nameField.getText().isEmpty()) {
                printError("Заполните поле наименования");
            } else if (loginField.getText().isEmpty()) {
                printError("Заполните поле ключа");
            } else if (passwordField.getText().isEmpty()) {
                printError("Заполните поля пароля");
            } else {
                if (mode == Mode.NEW) {
                    Account account = Account.builder()
                            .name(nameField.getText())
                            .login(loginField.getText())
                            .password(passwordField.getText())
                            .build();

                    var secret = secretDao.createAccount(account);
                    secretDao.addSecretToWorker(uiStateComponent.getActiveUser().getId(), secret.getId());
                } else {
                    Account account = Account.builder()
                            .id(uiStateComponent.getSecret().getId())
                            .name(nameField.getText())
                            .login(loginField.getText())
                            .password(passwordField.getText())
                            .secret(uiStateComponent.getSecret()).build();

                    secretDao.updateAccount(account);
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
                secretDao.removeAccount(uiStateComponent.getSecret().getId());
            }
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelSecretsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });

        loginBufferButton.setOnAction(actionEvent -> {
            String key = loginField.getText();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(key);
            clipboard.setContent(content);
        });

        passwordBufferButton.setOnAction(actionEvent -> {
            String key = passwordField.getText();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(key);
            clipboard.setContent(content);
        });
    }
}

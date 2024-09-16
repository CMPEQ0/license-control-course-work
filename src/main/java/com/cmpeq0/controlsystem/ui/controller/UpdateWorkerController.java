package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.SecretDao;
import com.cmpeq0.controlsystem.dao.WorkerDao;
import com.cmpeq0.controlsystem.entity.Secret;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.exception.DataException;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelWorkersReadyEvent;
import com.cmpeq0.controlsystem.utils.Encoder;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UpdateWorkerController {

    private final WorkerDao workerDao;
    private final UiStateComponent uiStateComponent;
    private final ApplicationContext applicationContext;
    private final SecretDao secretDao;

    @FXML
    public Label idLabel;

    @FXML
    public TextField nameField;

    @FXML
    public TextField departmentField;

    @FXML
    public TextField positionField;

    @FXML
    public TextField loginField;

    @FXML
    public TextField passwordField;

    @FXML
    public TextField secretField;

    @FXML
    public TreeTableView<Secret> table;

    @FXML
    public Button deleteButton;

    @FXML
    public Button assignButton;

    @FXML
    public Button saveButton;

    @FXML
    public Button changeButton;

    @FXML
    public Button revokeButton;

    @FXML
    Label errorLabel;

    public enum Mode {
        NEW,
        UPDATE
    }

    @Setter
    private Mode mode = Mode.NEW;

    private void printTable() {
        var columns = table.getColumns();
        uiStateComponent.setWorker(workerDao.findById(uiStateComponent.getWorker().getId()));
        columns.get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        columns.get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        columns.get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        var root = new TreeItem<Secret>();
        for (var secret : uiStateComponent.getWorker().getSecrets()) {
            TreeItem<Secret> item = new TreeItem<>(secret);
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

        if (uiStateComponent.getWorker() != null) {
            mode = Mode.UPDATE;
        } else {
            mode = Mode.NEW;
        }

        if (mode == Mode.NEW) {
            changeButton.setVisible(false);
            secretField.setVisible(false);
            assignButton.setVisible(false);
            revokeButton.setVisible(false);
        } else {
            idLabel.setText(uiStateComponent.getWorker().getId().toString());
            nameField.setText(uiStateComponent.getWorker().getName());
            departmentField.setText(uiStateComponent.getWorker().getDepartment());
            positionField.setText(uiStateComponent.getWorker().getPosition());
            loginField.setText(uiStateComponent.getWorker().getLogin());

            //printing secrets
            printTable();
        }
        saveButton.setOnAction(actionEvent -> {
            if (validateFields()) {
                saveWorker();
            }
        });
        changeButton.setOnAction(actionEvent -> {
            if (validateFields()) {
                workerDao.updateWorkerPassword(loginField.getText(), Encoder.encrypt(passwordField.getText()));
            }
        });

        deleteButton.setOnAction(actionEvent -> {
            if (mode != Mode.NEW) {
                secretDao.clearWorkerSecrets(uiStateComponent.getWorker().getId());
                workerDao.removeWorker(uiStateComponent.getWorker().getId());
            }

            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelWorkersReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });

        assignButton.setOnAction(actionEvent -> {
            if (validateTargetId()) {
                long id = Long.parseLong(secretField.getText());
                Secret secret = secretDao.findSecretById(id);
                if (secret == null) {
                    printError("Ресурс не найден");
                } else {
                    secretDao.addSecretToWorker(uiStateComponent.getWorker().getId(), id);
                }
            } else {
                printError("Неверный формат id");
            }
            printTable();
        });

        revokeButton.setOnAction(actionEvent -> {
            if (validateTargetId()) {
                long id = Long.parseLong(secretField.getText());
                Secret secret = secretDao.findSecretById(id);
                if (secret == null) {
                    printError("Ресурс не найден");
                } else {
                    secretDao.removeSecretFromWorker(uiStateComponent.getWorker().getId(), id);
                }
            } else {
                printError("Неверный формат id");
            }
            printTable();
        });
    }

    private void saveWorker() {
        if (this.mode == Mode.NEW) {
            Worker worker = Worker.builder()
                    .name(nameField.getText())
                    .department(departmentField.getText())
                    .position(positionField.getText())
                    .login(loginField.getText())
                    .role(Worker.Role.USER)
                    .build();
            try {
                workerDao.createWorker(worker, Encoder.encrypt(passwordField.getText()));
                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new ControlPanelWorkersReadyEvent(uiStateComponent.getStage()));
                if (old != null) {
                    old.hide();
                }
            } catch (DataException e) {
                printError(e.getMessage());
            }
        } else {
            Worker worker = Worker.builder()
                    .id(uiStateComponent.getWorker().getId())
                    .name(nameField.getText())
                    .department(departmentField.getText())
                    .position(positionField.getText())
                    .login(loginField.getText())
                    .role(uiStateComponent.getWorker().getRole())
                    .secrets(uiStateComponent.getWorker().getSecrets()).build();
            try {
                workerDao.updateWorker(worker);
                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new ControlPanelWorkersReadyEvent(uiStateComponent.getStage()));
                if (old != null) {
                    old.hide();
                }
            } catch (DataException e) {
                printError(e.getMessage());
            }
        }
    }

    private void printError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void printError() {
        printError("");
    }

    private boolean validateName() {
        String name = nameField.getText();
        String regex = "^[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean validateDepartment() {
        return !departmentField.getText().isEmpty();
    }

    private boolean validatePosition() {
        return !positionField.getText().isEmpty();
    }

    private boolean validateLogin() {
        String regex = "^[a-zA-Z][a-zA-Z0-9._]{4,19}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(loginField.getText());
        return matcher.matches();
    }

    private boolean validatePassword() {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(passwordField.getText());
        return matcher.matches();
    }

    private boolean validateTargetId() {
        String id = secretField.getText();
        if (id == null || !id.matches("\\d+")) {
            printError();
            return false;
        }
        return true;
    }

    private boolean validateFields() {
        if (!validateName()) {
            printError("Неверный формат ФИО");
        } else if (!validateDepartment()) {
            printError("Неверный формат департамента");
        } else if (!validatePosition()) {
            printError("Неверный формат должности");
        } else if (!validateLogin()) {
            printError("Неверный формат логина");
        } else if ((this.mode == Mode.NEW || !passwordField.getText().isEmpty()) && !validatePassword()) {
            printError("Неверный формат пароля");
        } else {
            printError();
            return true;
        }
        return false;
    }

}

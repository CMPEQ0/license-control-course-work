package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.SecretDao;
import com.cmpeq0.controlsystem.entity.Room;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelRoomsReadyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UpdateRoomController {

    private final UiStateComponent uiStateComponent;
    private final ApplicationContext applicationContext;
    private final SecretDao secretDao;

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
    public Label statusLabel;

    @FXML
    public TextField nameField;

    @FXML
    public TreeTableView<Worker> table;

    @FXML
    public Button saveButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button doorButton;

    private void printError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void printError() {
        printError("");
    }

    private void setOpened() {
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("Открыто");
        doorButton.setText("Закрыть");
    }

    private void setClosed() {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText("Закрыто");
        doorButton.setText("Открыть");
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
            boolean closed = uiStateComponent.getSecret().getRoom().isClosed();
            if (closed) {
                setClosed();
            } else {
                setOpened();
            }
            //printing secrets
            printTable();
        } else {
            setClosed();
            doorButton.setVisible(false);
        }

        saveButton.setOnAction(actionEvent -> {
            if (nameField.getText().isEmpty()) {
                printError("Заполните поле наименования");
            } else {
                if (mode == Mode.NEW) {
                    Room room = Room.builder().name(nameField.getText()).build();
                    var secret = secretDao.createRoom(room);
                    secretDao.addSecretToWorker(uiStateComponent.getActiveUser().getId(), secret.getId());
                } else {
                    Room room = Room.builder()
                            .id(uiStateComponent.getSecret().getRoom().getId())
                            .closed(uiStateComponent.getSecret().getRoom().isClosed())
                            .name(nameField.getText())
                            .secret(uiStateComponent.getSecret()).build();
                    secretDao.updateRoom(room);
                }

                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new ControlPanelRoomsReadyEvent(uiStateComponent.getStage()));
                if (old != null) {
                    old.hide();
                }
            }
        });

        deleteButton.setOnAction(actionEvent -> {
            if (mode == Mode.UPDATE) {
                secretDao.removeRoom(uiStateComponent.getSecret().getId());
            }
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelRoomsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });

        doorButton.setOnAction(actionEvent -> {
            boolean closed = uiStateComponent.getSecret().getRoom().isClosed();
            if (closed) {
                setOpened();
                secretDao.openRoom(uiStateComponent.getSecret().getRoom());
            } else {
                setClosed();
                secretDao.closeRoom(uiStateComponent.getSecret().getRoom());
            }
        });
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

}

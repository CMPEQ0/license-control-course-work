package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.SecretDao;
import com.cmpeq0.controlsystem.entity.Room;
import com.cmpeq0.controlsystem.entity.Secret;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelRoomsReadyEvent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelSecretsReadyEvent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelWorkersReadyEvent;
import com.cmpeq0.controlsystem.ui.event.UpdateRoomStageReadyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomPanelController {

    private final ApplicationContext applicationContext;
    private final UiStateComponent uiStateComponent;
    private final SecretDao secretDao;

    @FXML
    public Button dataButton;

    @FXML
    public Button roomButton;

    @FXML
    public Button workersButton;

    @FXML
    public TreeTableView<Secret> table;

    @FXML
    public Button createButton;

    @FXML
    public Button openButton;

    @FXML
    public Button closeButton;


    private void printTable() {
        var columns = table.getColumns();
        columns.get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        columns.get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        columns.get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("statusString"));

        List<Secret> rooms = secretDao.findAllRooms().stream().map(Room::getSecret).toList();
        var root = new TreeItem<Secret>();
        for (var room : rooms) {
            TreeItem<Secret> item = new TreeItem<>(room);
            root.getChildren().add(item);
        }
        root.setExpanded(true);
        table.setShowRoot(false);
        table.setRoot(root);

        table.setOnMouseClicked(mouseEvent -> {
            TreeItem<Secret> selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Room selectedRoom = selectedItem.getValue().getRoom();
                uiStateComponent.setSecret(selectedRoom.getSecret());
                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new UpdateRoomStageReadyEvent(uiStateComponent.getStage(), selectedRoom.getSecret().getId()));
                if (old != null) {
                    old.hide();
                }
            }
        });
    }

    @FXML
    public void initialize() {
        printTable();

        workersButton.setOnAction(actionEvent -> {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelWorkersReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });
        roomButton.setOnAction(actionEvent -> {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelRoomsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });
        dataButton.setOnAction(actionEvent -> {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelSecretsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });

        createButton.setOnAction(actionEvent -> {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            uiStateComponent.setSecret(null);
            applicationContext.publishEvent(new UpdateRoomStageReadyEvent(uiStateComponent.getStage(), -1));
            if (old != null) {
                old.hide();
            }
        });

        openButton.setOnAction(actionEvent -> {
            secretDao.openAllRooms();
            printTable();
        });

        closeButton.setOnAction(actionEvent -> {
            secretDao.closeAllRooms();
            printTable();
        });
    }


}

package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.WorkerDao;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelRoomsReadyEvent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelSecretsReadyEvent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelWorkersReadyEvent;
import com.cmpeq0.controlsystem.ui.event.UpdateWorkerStageReadyEvent;
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
public class WorkersPanelController {

    private final UiStateComponent uiStateComponent;
    private final ApplicationContext applicationContext;
    private final WorkerDao workerDao;

    @FXML
    public Button dataButton;

    @FXML
    public Button roomButton;

    @FXML
    public Button workersButton;

    @FXML
    public Button createButton;

    @FXML
    public TreeTableView<Worker> table;

    private void printTable() {

        var columns = table.getColumns();
        columns.get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        columns.get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        columns.get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("department"));
        columns.get(3).setCellValueFactory(new TreeItemPropertyValueFactory<>("position"));

        List<Worker> workers = workerDao.findAll();
        //System.out.println(workers.size());
        var root = new TreeItem<Worker>();
        for (var worker : workers) {
            TreeItem<Worker> item = new TreeItem<>(worker);
            root.getChildren().add(item);
        }
        root.setExpanded(true);
        table.setShowRoot(false);
        table.setRoot(root);

        table.setOnMouseClicked(mouseEvent -> {
            TreeItem<Worker> selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Worker selectedWorker = selectedItem.getValue();
                uiStateComponent.setWorker(selectedWorker);
                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new UpdateWorkerStageReadyEvent(uiStateComponent.getStage(), selectedWorker.getId()));
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
            uiStateComponent.setWorker(null);
            applicationContext.publishEvent(new UpdateWorkerStageReadyEvent(uiStateComponent.getStage(), -1));
            if (old != null) {
                old.hide();
            }
        });
    }


}

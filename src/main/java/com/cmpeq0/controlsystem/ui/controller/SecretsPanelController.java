package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.SecretDao;
import com.cmpeq0.controlsystem.entity.Secret;
import com.cmpeq0.controlsystem.entity.Worker;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.*;
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
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecretsPanelController {

    private final UiStateComponent uiStateComponent;
    private final ApplicationContext applicationContext;
    private final SecretDao secretDao;

    @FXML
    public Button dataButton;

    @FXML
    public Button roomButton;

    @FXML
    public Button workersButton;

    @FXML
    public Button createLicenseButton;

    @FXML
    public Button createAccountButton;

    @FXML
    public TreeTableView<Secret> table;

    @FXML
    public void initialize() {
        printTable();

        if (uiStateComponent.getActiveUser().getRole() != Worker.Role.ADMIN) {
            workersButton.setVisible(false);
            roomButton.setVisible(false);
            dataButton.setVisible(false);
        }

        workersButton.setOnAction(actionEvent ->
        {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelWorkersReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });
        roomButton.setOnAction(actionEvent ->
        {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelRoomsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });
        dataButton.setOnAction(actionEvent ->
        {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new ControlPanelSecretsReadyEvent(uiStateComponent.getStage()));
            if (old != null) {
                old.hide();
            }
        });

        createLicenseButton.setOnAction(actionEvent -> {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setSecret(null);
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new UpdateLicenseStageReadyEvent(uiStateComponent.getStage(), -1));
            if (old != null) {
                old.hide();
            }
        });

        createAccountButton.setOnAction(actionEvent -> {
            Stage old = uiStateComponent.getStage();
            uiStateComponent.setSecret(null);
            uiStateComponent.setStage(new Stage());
            applicationContext.publishEvent(new UpdateAccountStageReadyEvent(uiStateComponent.getStage(), -1));
            if (old != null) {
                old.hide();
            }
        });
    }

    private void printTable() {
        var columns = table.getColumns();
        columns.get(0).setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        columns.get(1).setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        columns.get(2).setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));

        List<Secret> secrets = secretDao.findAllLicensesAndAccounts();

        if (uiStateComponent.getActiveUser().getRole() != Worker.Role.ADMIN) {
            secrets = secrets.stream()
                    .filter(secret ->
                            secret.getWorkers().stream().anyMatch(worker ->
                                    Objects.equals(worker.getId(), uiStateComponent.getWorker().getId()))
                    ).toList();
        }
        var root = new TreeItem<Secret>();
        for (var secret : secrets) {
            TreeItem<Secret> item = new TreeItem<>(secret);
            root.getChildren().add(item);
        }
        root.setExpanded(true);
        table.setShowRoot(false);
        table.setRoot(root);

        table.setOnMouseClicked(mouseEvent -> {
            TreeItem<Secret> selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Secret selectedSecret = selectedItem.getValue();
                uiStateComponent.setSecret(selectedSecret);

                if (selectedSecret.getType() == Secret.SecretType.LICENCE) {
                    Stage old = uiStateComponent.getStage();
                    uiStateComponent.setStage(new Stage());
                    applicationContext.publishEvent(new UpdateLicenseStageReadyEvent(uiStateComponent.getStage(), selectedSecret.getId()));
                    if (old != null) {
                        old.hide();
                    }
                } else if (selectedSecret.getType() == Secret.SecretType.ACCOUNT) {
                    Stage old = uiStateComponent.getStage();
                    uiStateComponent.setStage(new Stage());
                    applicationContext.publishEvent(new UpdateAccountStageReadyEvent(uiStateComponent.getStage(), selectedSecret.getId()));
                    if (old != null) {
                        old.hide();
                    }
                }
            }
        });
    }

}

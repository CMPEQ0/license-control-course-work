package com.cmpeq0.controlsystem.ui.controller;

import com.cmpeq0.controlsystem.dao.WorkerDao;
import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelSecretsReadyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginFormController {

    private final UiStateComponent uiStateComponent;
    private final ApplicationContext applicationContext;
    private final WorkerDao workerDao;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button enterButton;

    @FXML
    public Label errorLabel;


    private void printError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void printError() {
        printError("");
    }


    @FXML
    public void initialize() {
        workerDao.startDefault();

        errorLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: red;");

        enterButton.setOnAction(actionEvent -> {
            boolean check = workerDao.matchLoginPassword(loginField.getText(), passwordField.getText());
            if (check) {
                uiStateComponent.setActiveUser(workerDao.findWorkerByLogin(loginField.getText()));
                Stage old = uiStateComponent.getStage();
                uiStateComponent.setStage(new Stage());
                applicationContext.publishEvent(new ControlPanelSecretsReadyEvent(uiStateComponent.getStage()));
                if (old != null) {
                    old.hide();
                }
            } else {
                printError("Неверный логин или пароль");
            }
        });
    }
}

package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.LoginStageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class LoginStageReadyListener extends StageReadyEventListener<LoginStageReadyEvent>{

    public LoginStageReadyListener(
            @Value("${spring.application.ui.title.login}") String title,
            @Value("classpath:/LoginForm.fxml") Resource resource,
            ApplicationContext applicationContext, UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }

}

package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelSecretsReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ControlPanelSecretStageReadyListener extends StageReadyEventListener<ControlPanelSecretsReadyEvent> {

    public ControlPanelSecretStageReadyListener(
            @Value("${spring.application.ui.title.panel-secret}") String title,
            @Value("classpath:/ControlPanelSecrets.fxml") Resource resource,
            ApplicationContext applicationContext, UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }
}

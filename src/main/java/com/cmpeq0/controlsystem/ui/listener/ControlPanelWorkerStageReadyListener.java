package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelWorkersReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ControlPanelWorkerStageReadyListener extends StageReadyEventListener<ControlPanelWorkersReadyEvent> {

    public ControlPanelWorkerStageReadyListener(
            @Value("${spring.application.ui.title.panel-worker}") String title,
            @Value("classpath:/ControlPanelWorkers.fxml") Resource resource,
            ApplicationContext applicationContext, UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }
}

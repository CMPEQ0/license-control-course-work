package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.UpdateWorkerStageReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UpdateWorkerStageReadyListener extends StageReadyEventListener<UpdateWorkerStageReadyEvent> {
    public UpdateWorkerStageReadyListener(
            @Value("${spring.application.ui.title.worker-data}") String title,
            @Value("classpath:/UpdateWorkerForm.fxml") Resource resource,
            ApplicationContext applicationContext,
            UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }
}

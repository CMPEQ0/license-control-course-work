package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.UpdateLicenseStageReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UpdateLicenseStageReadyListener extends StageReadyEventListener<UpdateLicenseStageReadyEvent> {


    public UpdateLicenseStageReadyListener(@Value("${spring.application.ui.title.license-data}")String title,
                @Value("classpath:/UpdateLicenceForm.fxml") Resource resource,
                ApplicationContext applicationContext,
                UiStateComponent uiStateComponent) {
            super(title, resource, applicationContext, uiStateComponent);
        }

}

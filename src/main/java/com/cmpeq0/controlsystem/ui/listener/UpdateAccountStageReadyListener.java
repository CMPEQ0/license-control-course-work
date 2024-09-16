package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.UpdateAccountStageReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UpdateAccountStageReadyListener extends StageReadyEventListener<UpdateAccountStageReadyEvent> {


    public UpdateAccountStageReadyListener(@Value("${spring.application.ui.title.account-data}")String title,
                                           @Value("classpath:/UpdateAccountForm.fxml") Resource resource,
                                           ApplicationContext applicationContext,
                                           UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }

}

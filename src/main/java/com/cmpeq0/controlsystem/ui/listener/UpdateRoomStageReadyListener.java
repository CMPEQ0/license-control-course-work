package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.UpdateRoomStageReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UpdateRoomStageReadyListener extends StageReadyEventListener<UpdateRoomStageReadyEvent> {


    public UpdateRoomStageReadyListener(@Value("${spring.application.ui.title.room-data}")String title,
                                        @Value("classpath:/UpdateRoomForm.fxml")Resource resource,
                                        ApplicationContext applicationContext,
                                        UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }
}

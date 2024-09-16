package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.ControlPanelRoomsReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ControlPanelRoomStageReadyListener extends StageReadyEventListener<ControlPanelRoomsReadyEvent> {

    public ControlPanelRoomStageReadyListener(
            @Value("${spring.application.ui.title.panel-room}") String title,
            @Value("classpath:/ControlPanelRooms.fxml") Resource resource,
            ApplicationContext applicationContext, UiStateComponent uiStateComponent) {
        super(title, resource, applicationContext, uiStateComponent);
    }
}

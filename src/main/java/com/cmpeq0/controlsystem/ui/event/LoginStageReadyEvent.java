package com.cmpeq0.controlsystem.ui.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class LoginStageReadyEvent extends StageReadyEvent {

    public LoginStageReadyEvent(Stage source) {
        super(source);
    }
}

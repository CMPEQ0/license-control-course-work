package com.cmpeq0.controlsystem.ui.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Stage source) {
        super(source);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }

}

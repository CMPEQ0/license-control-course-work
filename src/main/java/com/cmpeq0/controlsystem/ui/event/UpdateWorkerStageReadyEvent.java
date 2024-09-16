package com.cmpeq0.controlsystem.ui.event;

import javafx.stage.Stage;
import lombok.Getter;


@Getter
public class UpdateWorkerStageReadyEvent extends StageReadyEvent {

    private final long id;

    public UpdateWorkerStageReadyEvent(Stage source, long id) {
        super(source);
        this.id = id;
    }

}

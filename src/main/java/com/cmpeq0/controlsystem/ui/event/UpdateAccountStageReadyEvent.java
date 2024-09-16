package com.cmpeq0.controlsystem.ui.event;

import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class UpdateAccountStageReadyEvent extends StageReadyEvent{

    private final long id;

    public UpdateAccountStageReadyEvent(Stage source, long id) {
        super(source);
        this.id = id;
    }

}

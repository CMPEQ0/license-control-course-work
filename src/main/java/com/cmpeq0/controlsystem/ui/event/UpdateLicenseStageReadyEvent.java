package com.cmpeq0.controlsystem.ui.event;

import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class UpdateLicenseStageReadyEvent extends StageReadyEvent {

    private final long id;

    public UpdateLicenseStageReadyEvent(Stage source, long id) {
        super(source);
        this.id = id;
    }

}

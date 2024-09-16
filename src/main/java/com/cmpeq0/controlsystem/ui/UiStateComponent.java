package com.cmpeq0.controlsystem.ui;

import com.cmpeq0.controlsystem.entity.Room;
import com.cmpeq0.controlsystem.entity.Secret;
import com.cmpeq0.controlsystem.entity.Worker;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UiStateComponent {

    private Stage stage;

    private Scene scene;

    private Worker activeUser;

    private Worker worker;

    private Secret secret;

    public void update(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

}

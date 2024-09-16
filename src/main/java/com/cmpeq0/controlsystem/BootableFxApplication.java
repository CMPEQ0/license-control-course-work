package com.cmpeq0.controlsystem;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootableFxApplication {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

}

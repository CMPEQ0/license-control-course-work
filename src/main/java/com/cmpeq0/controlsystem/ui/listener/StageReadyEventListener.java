package com.cmpeq0.controlsystem.ui.listener;

import com.cmpeq0.controlsystem.ui.UiStateComponent;
import com.cmpeq0.controlsystem.ui.event.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;

public abstract class StageReadyEventListener<T extends StageReadyEvent> implements ApplicationListener<T> {
    protected final String formTitle;
    protected final Resource fxml;
    protected final ApplicationContext applicationContext;
    protected final UiStateComponent uiStateComponent;

    public StageReadyEventListener(String title,
                                   Resource resource,
                                   ApplicationContext applicationContext,
                                   UiStateComponent uiStateComponent) {
        this.formTitle = title;
        this.fxml = resource;
        this.applicationContext = applicationContext;
        this.uiStateComponent = uiStateComponent;
    }


    @Override
    public void onApplicationEvent(T event) {
        try {
            Stage stage = event.getStage();
            URL url = this.fxml.getURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(this.formTitle);
            stage.setResizable(false);
            uiStateComponent.setStage(stage);
            uiStateComponent.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

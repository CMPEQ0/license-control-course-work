package com.cmpeq0.controlsystem;

import com.cmpeq0.controlsystem.ui.event.LoginStageReadyEvent;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


public class JavaFxApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() throws Exception {

		ApplicationContextInitializer<GenericApplicationContext> initializer = applicationContext -> {
            applicationContext.registerBean(Application.class, () -> JavaFxApplication.this);
            applicationContext.registerBean(Parameters.class, this::getParameters);
            applicationContext.registerBean(HostServices.class, this::getHostServices);
        };

        this.context = new SpringApplicationBuilder()
				.sources(BootableFxApplication.class)
				.initializers(initializer)
				.run(getParameters().getRaw().toArray(new String[0]));
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.context.publishEvent(new LoginStageReadyEvent(stage));
	}

	@Override
	public void stop() throws Exception {
		this.context.close();
		Platform.exit();
	}
}

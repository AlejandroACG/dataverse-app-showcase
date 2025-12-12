package com.alejandroacg.dataverseappshowcase;

import atlantafx.base.theme.PrimerDark;
import com.alejandroacg.dataverseappshowcase.tasks.FxExecutor;
import com.alejandroacg.dataverseappshowcase.utils.GlobalExceptionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.MAIN_FORM_HEIGHT;
import static com.alejandroacg.dataverseappshowcase.config.UIConstants.MAIN_FORM_WIDTH;

/**
 * JavaFX entry point for the application.
 * <p>
 * This launcher is responsible for:
 * <ul>
 *   <li>Bootstrapping the Spring ApplicationContext</li>
 *   <li>Initializing JavaFX and loading the main UI</li>
 *   <li>Applying global exception handling</li>
 *   <li>Ensuring proper shutdown of both JavaFX and Spring</li>
 * </ul>
 * <p>
 * Spring Boot is started manually (instead of via {@code SpringApplication.run})
 * so that JavaFX and Spring can coexist cleanly without interfering with each
 * other's lifecycle.
 * </p>
 */
public class AppLauncher extends Application {

    /**
     * Reference to the Spring ApplicationContext.
     * Initialized during {@link #init()} and closed during {@link #stop()}.
     */
    private ConfigurableApplicationContext context;

    /**
     * Initializes the Spring context before JavaFX starts.
     * <p>
     * JavaFX calls {@code init()} on a non-FX thread, making it a safe place
     * to bootstrap Spring without blocking UI startup. The application is set
     * to non-headless mode to allow JavaFX integration.
     * </p>
     */
    @Override
    public void init() {
        context = new SpringApplicationBuilder(DataverseApplication.class)
                .headless(false) // Allow JavaFX integration
                .run();
    }

    /**
     * Called once JavaFX is ready to display the primary window.
     * <p>
     * Responsibilities:
     * <ul>
     *   <li>Install a global uncaught-exception handler</li>
     *   <li>Apply the global visual theme (PrimerDark)</li>
     *   <li>Load the main FXML layout and associated CSS</li>
     *   <li>Configure and show the primary Stage</li>
     * </ul>
     * </p>
     *
     * @param stage Main JavaFX window
     */
    @Override
    public void start(Stage stage) {
        try {
            // Global exception handler for all threads
            GlobalExceptionHandler.setup();

            // Apply application-wide UI theme
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

            // Load root UI from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Attach global stylesheet
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/static/css/style.css")
                    ).toExternalForm()
            );

            // Configure window properties
            stage.setTitle("Dataverse");
            stage.getIcons().add(
                    new Image(
                            Objects.requireNonNull(
                                    getClass().getResourceAsStream("/static/img/icon.png")
                            )
                    )
            );
            stage.setScene(scene);
            stage.setMinWidth(MAIN_FORM_WIDTH);
            stage.setMinHeight(MAIN_FORM_HEIGHT);

            stage.show();

        } catch (Exception e) {
            // Fatal UI initialization issue: report and exit gracefully
            GlobalExceptionHandler.handle(e);
            stop();
        }
    }

    /**
     * Fully shuts down the application, ensuring Spring and JavaFX terminate cleanly.
     * <p>
     * Responsibilities:
     * <ul>
     *   <li>Close the Spring context</li>
     *   <li>Shutdown the custom FxExecutor thread pool</li>
     *   <li>Exit JavaFX and force JVM shutdown</li>
     * </ul>
     * <p>
     * Forced JVM termination ({@code System.exit(0)}) ensures no lingering
     * non-daemon threads (e.g., database pools) prevent application exit.
     * </p>
     */
    @Override
    public void stop() {
        try {
            if (context != null) {
                context.close();
            }

            FxExecutor.shutdown();

        } catch (Exception e) {
            GlobalExceptionHandler.handle(e);
        } finally {
            Platform.exit();
            System.exit(0);
        }
    }
}

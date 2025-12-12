package com.alejandroacg.dataverseappshowcase.utils;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized utility for generating user-facing error and confirmation messages.
 *
 * <p>This class standardizes how validation and operational errors are translated into
 * readable UI alerts, ensuring consistent messaging across the application. It maps
 * {@link ManualErrorType} values to appropriate error messages and delegates UI
 * rendering to {@link AlertHelper}.</p>
 *
 * <p>All alert invocations are wrapped in {@link Platform#runLater(Runnable)} to ensure
 * they execute safely on the JavaFX Application Thread, regardless of the caller's
 * thread context.</p>
 */
public class AlertManager {

    private static final Logger logger = LoggerFactory.getLogger(AlertManager.class);

    /**
     * Builds a human-readable error message from the provided parameters and displays it
     * to the user. Each {@link ManualErrorType} maps to a standardized message format.
     *
     * @param entity     the name of the entity being validated (e.g., "Franchise")
     * @param attribute  the attribute that caused the error (e.g., "English Name")
     * @param value      the problematic value associated with the attribute
     * @param errorType  the specific error category
     */
    public static void handle(String entity, String attribute, String value, ManualErrorType errorType) {

        String message = switch (errorType) {

            case DUPLICATE_ENTRY ->
                    String.format("A %s with %s '%s' already exists.", entity, attribute, value);

            case REQUIRED_ENTRY ->
                    String.format("%s is required.", attribute);

            case LENGTH_EXCEEDED ->
                    String.format("%s can't exceed %s characters.", attribute, value);

            case NOT_FOUND ->
                    String.format("No %s found with value '%s'.", entity, value);

            case STORAGE_IN_ERROR ->
                    String.format("Failed to store image for %s.", entity);

            case STORAGE_OUT_ERROR ->
                    String.format("%s not found at: %s", attribute, value);

            case MISSING_HANDLER ->
                    String.format("No handler defined for: %s", entity);
        };

        show(message);
    }

    /**
     * Shows a confirmation dialog with OK/Cancel options and executes the provided
     * callback only if the user confirms.
     *
     * @param title      dialog title
     * @param content    dialog message body
     * @param onConfirm  runnable executed if user clicks the confirmation button
     */
    public static void confirm(String title, String content, Runnable onConfirm) {
        AlertHelper.showConfirmation(title, null, content, true)
                .ifPresent(response -> {
                    if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        onConfirm.run();
                    }
                });
    }

    /**
     * Logs and displays an error message using a standard error alert dialog.
     * Ensures UI thread safety by always delegating execution to JavaFX's UI thread.
     *
     * @param content the message shown in the alert dialog
     */
    private static void show(String content) {
        logger.warn("User-facing alert: {}", content);

        Platform.runLater(() ->
                AlertHelper.showError("Error", null, content)
        );
    }
}

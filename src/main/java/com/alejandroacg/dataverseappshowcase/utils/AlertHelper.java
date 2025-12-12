package com.alejandroacg.dataverseappshowcase.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utility class centralizing the creation and display of JavaFX alert dialogs.
 *
 * <p>This class provides strongly typed, reusable helper methods for showing
 * confirmation dialogs, informational alerts, warnings, and error messages.
 * It ensures consistent styling and button configuration across all UI components
 * that require user feedback.</p>
 *
 * <p>Certain operations—such as CSS class changes for destructive actions—are
 * intentionally deferred with {@link Platform#runLater(Runnable)} to guarantee
 * execution after the dialog's internal controls have been rendered.</p>
 */
public class AlertHelper {

    /**
     * Shows a confirmation dialog with customizable title, header, message,
     * and optional destructive-action styling.
     *
     * <p>The method replaces the default buttons with predictable "Confirm" and
     * "Cancel" actions. If {@code isDestructive} is set to {@code true},
     * the confirm button receives a "danger" CSS style class to visually indicate
     * the potential impact of the operation (e.g., deletion).</p>
     *
     * @param title         dialog title
     * @param header        optional header text (nullable)
     * @param content       main message body
     * @param isDestructive whether the confirmation action should be styled as destructive
     * @return an {@link Optional} containing the button pressed by the user
     */
    public static Optional<ButtonType> showConfirmation(
            String title,
            String header,
            String content,
            Boolean isDestructive
    ) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Custom button definitions for consistency across all confirmation dialogs
        ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        /*
         * Styling must be applied after the dialog is rendered, otherwise the lookup
         * may return null because controls are not yet part of the scene graph.
         */
        if (isDestructive) {
            Platform.runLater(() ->
                    alert.getDialogPane()
                            .lookupButton(confirm)
                            .getStyleClass()
                            .add("danger")
            );
        }

        return alert.showAndWait();
    }

    /**
     * Convenience method for showing a standard (non-destructive) confirmation dialog.
     *
     * @param title   dialog title
     * @param content message body
     * @return the selected {@link ButtonType}
     */
    public static Optional<ButtonType> showSimpleConfirmation(String title, String content) {
        return showConfirmation(title, null, content, false);
    }

    /**
     * Displays a basic informational alert.
     *
     * @param title   dialog title
     * @param header  optional header text (nullable)
     * @param content message body
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an error alert describing an unrecoverable or unexpected condition.
     *
     * @param title   dialog title
     * @param header  optional header text (nullable)
     * @param content message body
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a warning alert intended for recoverable or cautionary states.
     *
     * @param title   dialog title
     * @param header  optional header text (nullable)
     * @param content message body
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

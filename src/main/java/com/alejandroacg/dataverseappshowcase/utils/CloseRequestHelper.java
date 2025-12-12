package com.alejandroacg.dataverseappshowcase.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class that centralizes and simplifies logic related to closing
 * JavaFX windows, particularly when unsaved changes should be detected
 * before allowing the window to close.
 *
 * <p>This helper supports three main capabilities:
 * <ul>
 *     <li>Displaying confirmation dialogs when attempting to close a window with unsaved changes.</li>
 *     <li>Deferring attachment of close logic until the UI scene graph has been initialized.</li>
 *     <li>Closing multiple windows in a controlled and consistent manner.</li>
 * </ul>
 *
 * <p>The class is entirely static and not designed for instantiation.</p>
 */
public class CloseRequestHelper {

    // --------------------------------------------------------------------------------------------
    //  CLOSE CONFIRMATION API
    // --------------------------------------------------------------------------------------------

    /**
     * Attaches a close-request handler to the given stage. When the user attempts
     * to close the window, this method checks whether any tracked UI input fields
     * contain text or whether any additional external rules signal the presence of
     * unsaved changes.
     *
     * <p>If unsaved changes are detected, a confirmation dialog is shown. The window
     * will close only if the user explicitly confirms.</p>
     *
     * @param stage       the JavaFX window whose closing should be guarded
     * @param inputs      a list of UI nodes (typically {@link TextInputControl})
     *                    whose text contents determine whether changes exist
     * @param extraChecks additional boolean checks; each supplier returns true if
     *                    its associated condition represents an unsaved change
     */
    public static void requireConfirmationOnClose(Stage stage,
                                                  List<Node> inputs,
                                                  List<Supplier<Boolean>> extraChecks) {

        stage.setOnCloseRequest(event -> {
            if (hasUnsavedChanges(inputs, extraChecks)) {
                AlertHelper.showConfirmation(
                        "Confirm Exit",
                        null,
                        "You have unsaved changes. Are you sure you want to close?",
                        true
                ).ifPresent(response -> {
                    // Only allow closing if user clicked the OK button
                    if (response.getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                        event.consume();
                    }
                });
            }
        });
    }

    /**
     * Evaluates whether any tracked UI fields contain user-entered information
     * or whether any additional custom rules indicate that changes have been made.
     *
     * @param inputs      the UI nodes being monitored for text content
     * @param extraChecks custom change-detection rules
     * @return true if unsaved changes are present; false otherwise
     */
    private static boolean hasUnsavedChanges(List<Node> inputs, List<Supplier<Boolean>> extraChecks) {

        // Check text-based inputs
        for (Node node : inputs) {
            if (node instanceof TextInputControl control && !control.getText().isBlank()) {
                return true;
            }
        }

        // Check additional boolean rules
        for (Supplier<Boolean> check : extraChecks) {
            if (check.get()) return true;
        }

        return false;
    }

    // --------------------------------------------------------------------------------------------
    //  ATTACHING CLOSE LOGIC AFTER UI IS CONSTRUCTED
    // --------------------------------------------------------------------------------------------

    /**
     * Schedules the installation of {@link #requireConfirmationOnClose(Stage, List, List)}
     * until after the JavaFX scene has been fully initialized.
     *
     * <p>This is required for cases where the controller needs a reference to the
     * window, but at initialization time no scene or stage is attached to the node yet.</p>
     *
     * @param anyNode     any node in the target window's scene graph
     * @param inputs      UI inputs monitored for unsaved changes
     * @param extraChecks additional custom rules to evaluate
     */
    public static void setupWithDelay(Node anyNode,
                                      List<Node> inputs,
                                      List<Supplier<Boolean>> extraChecks) {

        Platform.runLater(() -> {
            Stage stage = (Stage) anyNode.getScene().getWindow();
            requireConfirmationOnClose(stage, inputs, extraChecks);
        });
    }

    // --------------------------------------------------------------------------------------------
    //  CLOSING MULTIPLE WINDOWS IN ORDER
    // --------------------------------------------------------------------------------------------

    /**
     * Attempts to close every stage in the provided collection.
     *
     * <p>This method is useful when performing application-wide refresh operations or
     * shutdown procedures that require closing multiple windows. Each window receives
     * a {@link WindowEvent#WINDOW_CLOSE_REQUEST}, ensuring that close-request handlers
     * (including unsaved-change confirmation prompts) are respected.</p>
     *
     * @param stages a collection of stages to close
     * @return true if all windows were successfully closed; false if any remain open
     */
    public static boolean tryCloseAll(Collection<Stage> stages) {

        // Copy required to avoid ConcurrentModificationException
        List<Stage> stageList = new ArrayList<>(stages);

        for (Stage stage : stageList) {
            if (stage.isShowing()) {
                stage.setIconified(false);
                stage.toFront();
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        }

        // If any stage is still open, return false
        return stageList.stream().noneMatch(Stage::isShowing);
    }
}

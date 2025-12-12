package com.alejandroacg.dataverseappshowcase.utils;

import com.alejandroacg.dataverseappshowcase.controllers.forms.FormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Centralized utility class responsible for managing all standalone windows in the application.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Opening new form windows (Create/Edit)</li>
 *     <li>Preventing duplicate windows of the same type</li>
 *     <li>Managing window lifecycle and refresh operations</li>
 *     <li>Applying consistent styling, sizing, and resource loading</li>
 * </ul>
 * <p>
 * Each open window is tracked in an internal registry keyed by a unique identifier,
 * allowing the application to bring existing windows to front instead of recreating them.
 */
public class WindowHelper {

    /**
     * Registry of currently open windows, keyed by an application-defined identifier.
     * Ensures single-instance behavior for windows such as forms or the "New" menu.
     */
    private static final Map<String, Stage> openWindows = new HashMap<>();

    // --------------------------------------------------------------------------------------------
    //  PUBLIC WINDOW OPENING API
    // --------------------------------------------------------------------------------------------

    /**
     * Opens a window defined by an arbitrary FXML path and identifies it using the given key.
     * If a window with the same key already exists and is visible, it is simply brought to the front.
     */
    public static void openWindow(String key, String fxmlPath, String title, int minWidth, int minHeight) {
        try {
            loadAndShowWindowWithController(key, fxmlPath, title, minWidth, minHeight);
        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    /**
     * Opens a new (Create) form window associated with a specific entity type.
     * The FXML path and window key are automatically derived from the type.
     */
    public static void openWindow(EntityType type, String title, int minWidth, int minHeight) {
        String fxmlPath = KeyBuilder.formFXMLPath(type);
        String key = KeyBuilder.windowNewFormKey(type);
        try {
            loadAndShowWindowWithController(key, fxmlPath, title, minWidth, minHeight);
        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    /**
     * Opens an Edit form window for the given entity type and ID.
     * After the window is created, the controller receives the entity ID via {@link FormController#onEdit(Long)}.
     */
    public static void openEditWindow(EntityType type, String title, int minWidth, int minHeight, Long entityId) {
        String fxmlPath = KeyBuilder.formFXMLPath(type);
        String key = KeyBuilder.windowEditFormKey(type, entityId);

        try {
            FormController controller = loadAndShowWindowWithController(key, fxmlPath, title, minWidth, minHeight);
            if (controller != null) {
                controller.onEdit(entityId);
            }
        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    // --------------------------------------------------------------------------------------------
    //  CORE LOADING LOGIC
    // --------------------------------------------------------------------------------------------

    /**
     * Core function responsible for loading an FXML file, creating a Stage,
     * and registering the window for lifecycle management.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>Checks for existing window with the same key and brings it forward if present</li>
     *     <li>Loads FXML and controller</li>
     *     <li>Applies stylesheets and app icon</li>
     *     <li>Registers window to the open-window tracking map</li>
     * </ul>
     *
     * @return The controller associated with the loaded FXML, or {@code null} if an existing window was reused.
     */
    private static <T> T loadAndShowWindowWithController(
            String key,
            String fxmlPath,
            String title,
            int minWidth,
            int minHeight
    ) throws IOException {

        // If the window already exists and is open, bring it to front
        if (openWindows.containsKey(key)) {
            Stage existing = openWindows.get(key);
            if (existing.isShowing()) {
                existing.setIconified(false);
                existing.toFront();
                return null;
            }
        }

        // Load UI and controller
        FXMLLoader loader = new FXMLLoader(WindowHelper.class.getResource(fxmlPath));
        Parent root = loader.load();

        // Create new window
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(
                new Image(Objects.requireNonNull(
                        WindowHelper.class.getResourceAsStream("/static/img/icon.png")
                ))
        );

        // Apply scene & global styles
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        WindowHelper.class.getResource("/static/css/style.css")
                ).toExternalForm()
        );

        stage.setScene(scene);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.centerOnScreen();

        // Track the window and remove it when closed
        stage.setOnHidden(e -> openWindows.remove(key));
        openWindows.put(key, stage);

        stage.show();

        // Store controller for refresh operations
        T controller = loader.getController();
        stage.setUserData(controller);

        return (T) controller;
    }

    // --------------------------------------------------------------------------------------------
    //  WINDOW STATE ACCESS
    // --------------------------------------------------------------------------------------------

    /**
     * Returns an unmodifiable collection of all currently open Stage instances.
     */
    public static Collection<Stage> getOpenWindows() {
        return openWindows.values();
    }
}

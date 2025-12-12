package com.alejandroacg.dataverseappshowcase.utils;

import javafx.scene.layout.StackPane;

/**
 * Utility class that adds clipboard-paste support to JavaFX nodes.
 *
 * <p>This helper enables listening for CTRL+V keyboard shortcuts at the
 * scene level, triggering a custom paste handler when detected. It is intended
 * for cases where users may paste images or text into UI components that do
 * not natively support clipboard interaction.</p>
 */
public class ClipboardHelper {

    /**
     * Installs a listener on the container's scene that monitors key presses.
     * When the user presses CTRL+V, the provided callback is executed.
     *
     * <p>This method attaches its logic only after the {@link javafx.scene.Scene}
     * becomes available, making it safe to call during controller initialization.</p>
     *
     * @param container the UI node whose scene will be used to register the key listener
     * @param onPaste   the action invoked when CTRL+V is detected
     */
    public static void enablePasteFromClipboard(StackPane container, Runnable onPaste) {

        container.sceneProperty().addListener((obs, oldScene, newScene) -> {

            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    // Detect CTRL+V
                    if (event.isControlDown()
                            && event.getCode().getName().equalsIgnoreCase("V")) {
                        onPaste.run();
                    }
                });
            }
        });
    }
}

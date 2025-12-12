package com.alejandroacg.dataverseappshowcase.utils;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Provides a simple JavaFX-based debouncing mechanism.
 *
 * <p>A debouncer delays the execution of an action until a specified period
 * has passed without receiving additional triggers. This is especially useful
 * for expensive UI operations such as live search, filtering, or repeated
 * input-driven updates.</p>
 *
 * <p>Example usage:
 * <pre>
 *     Debouncer debouncer = new Debouncer(250, () -> performSearch());
 *     searchField.textProperty().addListener((obs, old, val) -> debouncer.trigger());
 * </pre>
 *
 * <p>Implementation details:
 * <ul>
 *     <li>Internally uses a {@link PauseTransition} scheduled on the JavaFX Application Thread.</li>
 *     <li>Each call to {@link #trigger()} restarts the timer, ensuring the action
 *         only executes after a quiet period.</li>
 * </ul>
 */
public class Debouncer {

    /** Internal timer used to delay execution. */
    private final PauseTransition pause;

    /**
     * Creates a new debouncer.
     *
     * @param delayMs the delay in milliseconds to wait after the latest trigger
     * @param action  the action to execute once the debounce delay elapses
     */
    public Debouncer(int delayMs, Runnable action) {
        pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> action.run());
    }

    /**
     * Signals a new event that should reset the debounce timer.
     * The associated action will execute only if no further triggers occur
     * before the delay elapses.
     */
    public void trigger() {
        pause.stop();
        pause.playFromStart();
    }
}

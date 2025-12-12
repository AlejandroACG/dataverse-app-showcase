package com.alejandroacg.dataverseappshowcase.utils;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handling utility for both JavaFX and background threads.
 *
 * <p>This class centralizes error logging and user-facing alert reporting,
 * ensuring consistent behavior across all uncaught and explicitly handled
 * exceptions. It integrates with SLF4J for structured logging and delegates UI
 * reporting to {@link AlertHelper}.
 *
 * <p>The handler is installed via {@link #setup()}, which configures both:
 * <ul>
 *     <li>The JVM-wide default handler for uncaught exceptions</li>
 *     <li>The handler for the current JavaFX application thread</li>
 * </ul>
 *
 * <p>This mechanism is especially important when using asynchronous execution
 * utilities such as {@code FxExecutor}, where background exceptions could
 * otherwise fail silently.
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Installs this exception handler as the global default for all threads,
     * and explicitly for the JavaFX application thread.
     *
     * <p>This method should be called early in the application lifecycle,
     * typically inside {@code Application.start()} before initializing UI components.
     */
    public static void setup() {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        Thread.currentThread().setUncaughtExceptionHandler(new GlobalExceptionHandler());
    }

    /**
     * Handles uncaught exceptions by logging them and showing a user-friendly dialog.
     *
     * @param t the thread where the exception occurred
     * @param e the uncaught exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logAndShow("Uncaught exception in thread '" + t.getName() + "'", e);
    }

    /**
     * Handles exceptions that are explicitly forwarded to this handler.
     *
     * <p>This method is used throughout the application to unify exception
     * reporting logic from service calls, file operations, async tasks, etc.
     *
     * @param e the exception to handle
     */
    public static void handle(Throwable e) {
        logAndShow("Handled exception", e);
    }

    /**
     * Logs the exception via SLF4J and displays an error dialog to the user.
     *
     * <p>This method ensures that UI interaction occurs on the JavaFX
     * application thread via {@link Platform#runLater(Runnable)}.
     *
     * @param context a descriptive label indicating where the exception occurred
     * @param e       the exception being processed
     */
    private static void logAndShow(String context, Throwable e) {
        logger.error(context + ": {}", e.getMessage(), e);

        Platform.runLater(() -> {
            AlertHelper.showError(
                    "Error",
                    context,
                    e.getMessage() != null ? e.getMessage() : e.toString()
            );
        });
    }
}

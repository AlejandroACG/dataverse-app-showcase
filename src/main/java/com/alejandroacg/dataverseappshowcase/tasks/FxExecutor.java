package com.alejandroacg.dataverseappshowcase.tasks;

import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for executing background operations in a thread-safe and
 * JavaFX-compliant manner.
 *
 * <p>This class provides an abstraction over Java's {@link ExecutorService}
 * and JavaFX's {@link Task}, allowing developers to offload expensive work to
 * background threads while safely updating the UI on completion.
 *
 * <p>It centralizes asynchronous execution for the entire application and
 * ensures consistent behavior across all async tasks.
 */
public final class FxExecutor {

    /**
     * Thread pool used for executing background tasks. The number of worker
     * threads matches the number of available CPU cores to maximize throughput
     * without oversaturating the system.
     */
    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private FxExecutor() {}

    /**
     * Submits a JavaFX {@link Task} to the internal executor service.
     *
     * @param task the task to execute asynchronously
     */
    public static <T> void submit(Task<T> task) {
        EXECUTOR.submit(task);
    }

    /**
     * Shuts down the underlying executor service.
     *
     * <p>Called when the JavaFX application stops to ensure that background
     * threads are properly terminated and system resources are released.
     */
    public static void shutdown() {
        EXECUTOR.shutdown();
    }

    /**
     * Executes work asynchronously using an {@link AsyncOperation}.
     *
     * <p>This method provides full lifecycle hooks:
     * <ul>
     *     <li>{@code onRunning}: executed on the JavaFX thread before work begins</li>
     *     <li>{@code onSuccess}: executed on the JavaFX thread if work completes successfully</li>
     *     <li>{@code onError}: executed on the JavaFX thread if an exception occurs</li>
     *     <li>{@code onFinished}: always executed on the JavaFX thread after success or failure</li>
     * </ul>
     *
     * <p>The heavy computation is defined in {@code work}, which runs on a
     * background thread. UI callbacks execute safely on the main thread.
     *
     * @param work       the background computation to perform
     * @param onSuccess  callback executed when the computation succeeds
     * @param onError    callback executed when an exception occurs
     * @param onRunning  optional callback executed before the background task begins
     * @param onFinished optional callback executed after success or failure
     */
    public static <T> void runAsync(
            Supplier<T> work,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError,
            Runnable onRunning,
            Runnable onFinished
    ) {
        AsyncOperation<T> op = new AsyncOperation<>(work);

        // Executed on the JavaFX Application Thread before background work begins
        if (onRunning != null)
            op.setOnRunning(e -> onRunning.run());

        // Executed when the task succeeds
        op.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.accept(op.getValue());
            if (onFinished != null) onFinished.run();
        });

        // Executed if the task throws an exception
        op.setOnFailed(e -> {
            if (onError != null) onError.accept(op.getException());
            if (onFinished != null) onFinished.run();
        });

        // Execute the operation on the thread pool
        FxExecutor.submit(op);
    }

    /**
     * Convenience overload for async execution when only success and error
     * callbacks are needed.
     *
     * @param work      the background computation
     * @param onSuccess callback executed when the work succeeds
     * @param onError   callback executed when an exception occurs
     */
    public static <T> void runAsync(
            Supplier<T> work,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError
    ) {
        runAsync(work, onSuccess, onError, null, null);
    }
}

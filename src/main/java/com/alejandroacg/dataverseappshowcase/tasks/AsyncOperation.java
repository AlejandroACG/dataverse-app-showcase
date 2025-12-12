package com.alejandroacg.dataverseappshowcase.tasks;

import javafx.concurrent.Task;
import java.util.function.Supplier;

/**
 * A lightweight wrapper around a {@link Supplier} that integrates seamlessly
 * with JavaFX’s {@link Task} framework.
 *
 * <p>This class enables any background computation to be expressed as a
 * {@code Supplier<T>} and executed within JavaFX's task lifecycle, ensuring
 * that UI thread updates occur safely and predictably.
 *
 * <p>It is used internally by {@link FxExecutor} to unify asynchronous work
 * execution across the application.
 *
 * @param <T> the type of the result produced by the background operation
 */
public class AsyncOperation<T> extends Task<T> {

    /**
     * The computation to be executed in the background thread.
     */
    private final Supplier<T> action;

    /**
     * Constructs a new async operation wrapping the provided {@link Supplier}.
     *
     * @param action the background computation to perform
     */
    public AsyncOperation(Supplier<T> action) {
        this.action = action;
    }

    /**
     * Executes the supplied computation on a background thread.
     *
     * <p>This method is invoked automatically by JavaFX once the task has been
     * submitted to an executor. Exceptions thrown by the supplier will cause
     * the task to transition into the FAILED state.
     *
     * @return the result of the supplier
     */
    @Override
    protected T call() {
        return action.get();
    }
}

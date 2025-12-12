package com.alejandroacg.dataverseappshowcase.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Provides static access to the Spring {@link ApplicationContext}.
 * <p>
 * This class is used to retrieve Spring-managed beans from parts of the
 * application where dependency injection is not directly available,
 * such as JavaFX controllers.
 * </p>
 * <p>
 * The ApplicationContext is injected once by Spring during startup and
 * stored in a static field, enabling global access throughout the app.
 * </p>
 */
@Component
public class AppContextHolder implements ApplicationContextAware {

    /**
     * Static reference to the Spring ApplicationContext.
     * Used to retrieve beans programmatically.
     */
    private static ApplicationContext context;

    /**
     * Called automatically by Spring during initialization.
     * Stores the provided ApplicationContext for global access.
     *
     * @param ctx the initialized application context
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

    /**
     * Retrieves a bean of the specified type from the Spring context.
     *
     * @param beanClass the type of bean to retrieve
     * @param <T>       the generic type of the bean
     * @return the requested Spring bean instance
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}

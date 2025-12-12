package com.alejandroacg.dataverseappshowcase;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point.
 * <p>
 * Although this class contains no runtime logic, its presence is required
 * because the {@link SpringBootApplication} annotation triggers:
 * <ul>
 *   <li>Component scanning</li>
 *   <li>Auto-configuration</li>
 *   <li>Spring Boot initialization</li>
 * </ul>
 * In this project, the JavaFX launcher manually starts the Spring context
 * using a {@code SpringApplicationBuilder}, but Spring still requires this
 * annotated configuration class as the central application descriptor.
 * </p>
 */
@SpringBootApplication
public class DataverseApplication {
    // No implementation needed. The annotation defines the application context.
}

package com.alejandroacg.dataverseappshowcase.utils;

/**
 * Enumerates the different categories of manual (user-facing) validation and
 * operational errors that can occur throughout the application. These values
 * are used to classify error scenarios so that UI-level components can render
 * consistent and descriptive alerts.
 *
 * <p>Each constant represents a high-level error type rather than a specific
 * exception, allowing controllers and utility classes to abstract away the
 * details of validation logic.</p>
 */
public enum ManualErrorType {

    /**
     * Indicates that the operation failed because the entity being created or
     * updated would violate a uniqueness constraint (e.g., duplicated name).
     */
    DUPLICATE_ENTRY,

    /**
     * Indicates that a requested entity does not exist in the persistence layer.
     */
    NOT_FOUND,

    /**
     * Indicates that user input exceeded the allowed maximum length for a field.
     */
    LENGTH_EXCEEDED,

    /**
     * Represents failures while attempting to write data to storage (e.g., saving images).
     */
    STORAGE_IN_ERROR,

    /**
     * Represents failures while attempting to read data from storage (e.g., loading images).
     */
    STORAGE_OUT_ERROR,

    /**
     * Indicates that a mandatory user input field was left empty.
     */
    REQUIRED_ENTRY,

    /**
     * Indicates that no SearchHandler or component exists for a requested entity type.
     */
    MISSING_HANDLER
}

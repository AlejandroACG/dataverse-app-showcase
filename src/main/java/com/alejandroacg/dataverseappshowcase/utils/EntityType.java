package com.alejandroacg.dataverseappshowcase.utils;

import lombok.Getter;

/**
 * Defines the set of entity categories supported by the application.
 * Each enum constant provides:
 * <ul>
 *     <li>A singular display name (e.g., "Franchise")</li>
 *     <li>A plural display name (e.g., "Franchises")</li>
 *     <li>An entity status used for UI layout decisions
 *         (e.g., determining which form size to open)</li>
 * </ul>
 *
 * <p>This enum centralizes naming logic used throughout the UI,
 * tab management, window creation, and search workflows.</p>
 */
public enum EntityType {

    /** Represents a major content entity, displayed with large forms and cards. */
    FRANCHISE("Franchise", "Franchises", "entity"),

    /** Represents a tag-like categorization entity (small form, colored tag card). */
    GENRE("Genre", "Genres", "tag");

    @Getter
    private final String singular;

    @Getter
    private final String plural;

    /**
     * Indicates how the UI should treat the entity.
     * Typical values:
     * <ul>
     *     <li>"entity" – standard full entity with large forms</li>
     *     <li>"tag" – simplified tag-like entity using compact forms</li>
     * </ul>
     */
    // TODO Should be consulted to differentiate when opening forms or creating cards
    @Getter
    private final String entityStatus;

    EntityType(String singular, String plural, String entityStatus) {
        this.singular = singular;
        this.plural = plural;
        this.entityStatus = entityStatus;
    }

    /**
     * @return the singular name in lowercase form, used for paths and keys.
     */
    public String getSingularLowerCase() {
        return singular.toLowerCase();
    }

    /**
     * @return the plural name in lowercase form, used for folder paths and FXML routes.
     */
    public String getPluralLowerCase() {
        return plural.toLowerCase();
    }
}

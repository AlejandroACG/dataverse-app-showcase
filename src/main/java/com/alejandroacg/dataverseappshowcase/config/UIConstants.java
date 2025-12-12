package com.alejandroacg.dataverseappshowcase.config;

/**
 * Centralized UI-related constants used throughout the application.
 * <p>
 * This class defines fixed sizes, layout dimensions, and default UI
 * configuration values that help maintain visual consistency across
 * JavaFX views and controllers. It is designed as a non-instantiable
 * utility class.
 * </p>
 */
public final class UIConstants {

    // ──────────────────────────────────────────────
    // Main application frame dimensions
    // Used when initializing the primary JavaFX stage
    // ──────────────────────────────────────────────
    public static final int MAIN_FORM_WIDTH = 910;
    public static final int MAIN_FORM_HEIGHT = 600;

    // ──────────────────────────────────────────────
    // Default dimensions for general-purpose entity forms
    // (e.g., editing / creating medium-sized entities)
    // ──────────────────────────────────────────────
    public static final int BIG_FORM_WIDTH = 800;
    public static final int BIG_FORM_HEIGHT = 500;

    // ──────────────────────────────────────────────
    // Dimensions for smaller forms such as Genre or Tag editors
    // ──────────────────────────────────────────────
    public static final int SMALL_FORM_WIDTH = 500;
    public static final int SMALL_FORM_HEIGHT = 300;

    // ──────────────────────────────────────────────
    // Default size for pop-up menu windows
    // ──────────────────────────────────────────────
    public static final int MENU_WINDOW_WIDTH = 400;
    public static final int MENU_WINDOW_HEIGHT = 400;

    // ──────────────────────────────────────────────
    //  Default tag color used when no custom color is defined
    // ──────────────────────────────────────────────
    public static final String DEFAULT_TAG_COLOR = "#333333";

    // ──────────────────────────────────────────────
    //  Image selector preview area dimensions
    // ──────────────────────────────────────────────
    public static final int IMAGE_SELECTOR_WIDTH = 400;
    public static final int IMAGE_SELECTOR_HEIGHT = 600;

    // ──────────────────────────────────────────────
    // Dimensions for images displayed inside search result cards
    // ──────────────────────────────────────────────
    public static final int CARD_IMAGE_WIDTH = 150;
    public static final int CARD_IMAGE_HEIGHT = 90;

    // ──────────────────────────────────────────────
    // Pagination configuration for search results
    // ──────────────────────────────────────────────
    public static final int PAGE_SIZE_BIG_CARDS = 18;   // Used for Franchise cards
    public static final int PAGE_SIZE_SMALL_CARDS = 28; // Used for Genre cards

    /**
     * Private constructor to prevent instantiation.
     */
    private UIConstants() {}
}

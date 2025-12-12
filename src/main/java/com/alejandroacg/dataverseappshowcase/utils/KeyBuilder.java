package com.alejandroacg.dataverseappshowcase.utils;

/**
 * Centralized builder for generating stable, predictable keys and FXML paths used
 * throughout the application.
 *
 * <p>This class ensures that identifiers for windows, tabs, and FXML resources
 * follow consistent naming conventions, reducing the risk of mismatches and
 * making the UI system easier to maintain.
 *
 * <p>All methods are pure and side-effect-free. No state is stored.
 */
public class KeyBuilder {

    // ─────────────────────────────────────────────────────────────
    //  WINDOW IDENTIFIER KEYS
    // ─────────────────────────────────────────────────────────────

    /**
     * @return the constant key used for the "New Menu" pop-up window
     */
    public static String windowNewMenuKey() {
        return "new_menu";
    }

    /**
     * Builds a unique key for a "New Entity" form window.
     *
     * @param type the entity type (e.g., Franchise, Genre)
     * @return a stable identifier such as "franchise_form_new"
     */
    public static String windowNewFormKey(EntityType type) {
        return type.getSingularLowerCase() + "_form_new";
    }

    /**
     * Builds a unique key for an "Edit Entity" form window.
     *
     * <p>The key incorporates the entity ID to ensure that multiple edit windows
     * for different entities are treated as distinct.
     *
     * @param type the entity type
     * @param id   the entity identifier
     * @return a stable identifier such as "franchise_form_42"
     */
    public static String windowEditFormKey(EntityType type, Long id) {
        return type.getSingularLowerCase() + "_form_" + id;
    }

    // ─────────────────────────────────────────────────────────────
    //  WINDOW FXML PATHS
    // ─────────────────────────────────────────────────────────────

    /**
     * @return the classpath location of the "New Menu" view
     */
    public static String newMenuFXMLPath() {
        return "/views/new_menu.fxml";
    }

    /**
     * Builds the classpath path to the FXML file that defines the form for a given entity.
     *
     * @param type the entity type
     * @return e.g. "/views/franchise_form.fxml"
     */
    public static String formFXMLPath(EntityType type) {
        return "/views/" + type.getSingularLowerCase() + "_form.fxml";
    }

    // ─────────────────────────────────────────────────────────────
    //  TAB IDENTIFIER KEYS
    // ─────────────────────────────────────────────────────────────

    /**
     * Builds a unique key for a search tab corresponding to a specific entity type.
     *
     * @param type the entity type
     * @return e.g. "search_franchises"
     */
    public static String searchTabKey(EntityType type) {
        return "search_" + type.getPluralLowerCase();
    }

    /**
     * Builds a unique tab identifier for a details tab of a specific entity instance.
     *
     * @param type the entity type
     * @param id   the entity identifier
     * @return e.g. "franchise_details_42"
     */
    public static String detailsTabKey(EntityType type, Long id) {
        return type.getSingularLowerCase() + "_details_" + id;
    }

    // ─────────────────────────────────────────────────────────────
    //  TAB FXML PATHS
    // ─────────────────────────────────────────────────────────────

    /**
     * @return the classpath path for the search view shared by multiple entity types
     */
    public static String searchFXMLPath() {
        return "/views/search.fxml";
    }

    /**
     * Builds the classpath path to the FXML file that displays details for a given entity.
     *
     * @param type the entity type
     * @return e.g. "/views/franchise_details.fxml"
     */
    public static String detailsFXMLPath(EntityType type) {
        return "/views/" + type.getSingularLowerCase() + "_details.fxml";
    }
}

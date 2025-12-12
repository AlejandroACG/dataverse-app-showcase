package com.alejandroacg.dataverseappshowcase.controllers.details;

import com.alejandroacg.dataverseappshowcase.utils.EntityType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import lombok.Getter;
import lombok.Setter;

/**
 * Base controller for entity detail views.
 * <p>
 * This class provides shared functionality for screens that display
 * detailed information about a specific entity (e.g., Franchise, Genre).
 * It manages the common Edit/Delete buttons and stores metadata such as
 * the entity ID and entity type.
 * </p>
 */
public class DetailsController {

    /**
     * Button that triggers the editing workflow for the current entity.
     * Loaded via FXML in concrete detail controllers.
     */
    @FXML
    protected Button editButton;

    /**
     * Button that triggers the deletion workflow for the current entity.
     * Loaded via FXML in concrete detail controllers.
     */
    @FXML
    protected Button deleteButton;

    /**
     * Identifier of the entity displayed in this details view.
     */
    @Setter
    @Getter
    protected Long entityId;

    /**
     * Logical type of the entity (e.g., FRANCHISE, GENRE).
     * Subclasses set this based on their specific domain.
     */
    @Getter
    protected EntityType entityType;

    /**
     * Initializes the shared UI elements.
     * <p>
     * Ensures that action buttons use compact width sizing. This prevents
     * unwanted horizontal stretching when placed inside flexible layouts.
     * </p>
     */
    @FXML
    private void initialize() {
        if (editButton != null) {
            editButton.setMinWidth(Region.USE_PREF_SIZE);
            editButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            editButton.setMaxWidth(Region.USE_PREF_SIZE);
        }
        if (deleteButton != null) {
            deleteButton.setMinWidth(Region.USE_PREF_SIZE);
            deleteButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            deleteButton.setMaxWidth(Region.USE_PREF_SIZE);
        }
    }
}

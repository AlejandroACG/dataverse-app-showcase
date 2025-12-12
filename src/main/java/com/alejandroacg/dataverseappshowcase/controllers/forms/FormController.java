package com.alejandroacg.dataverseappshowcase.controllers.forms;

import com.alejandroacg.dataverseappshowcase.utils.EntityType;
import lombok.Getter;

/**
 * Base class for all form controllers (both creation and edition forms).
 * <p>
 * This abstract controller provides unified handling for the entity ID
 * associated with the form and exposes the entity type implemented by subclasses.
 * Any form that supports editing an existing record should call {@link #onEdit(Long)}
 * before loading its data.
 * </p>
 */
public abstract class FormController {

    /** Identifier of the entity currently being edited, or {@code null} when creating a new record. */
    @Getter
    protected Long entityId = null;

    /** Type of entity that this form is responsible for editing or creating. */
    @Getter
    protected EntityType entityType;

    /**
     * Called when the form is opened in edit mode.
     * Stores the ID of the entity to be edited; subclasses are expected to
     * load the data afterward.
     *
     * @param entityId The ID of the entity being edited.
     */
    public void onEdit(Long entityId) {
        this.entityId = entityId;
    }
}

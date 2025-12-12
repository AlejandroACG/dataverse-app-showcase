package com.alejandroacg.dataverseappshowcase.controllers.forms;

import com.alejandroacg.dataverseappshowcase.config.AppContextHolder;
import com.alejandroacg.dataverseappshowcase.models.Genre;
import com.alejandroacg.dataverseappshowcase.services.GenreService;
import com.alejandroacg.dataverseappshowcase.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.DEFAULT_TAG_COLOR;
import static com.alejandroacg.dataverseappshowcase.utils.EntityType.GENRE;

/**
 * Controller responsible for creating and editing Genre entities.
 * <p>
 * This form provides:
 * - Field validation
 * - Color selection for tag-style categories
 * - Character count enforcement
 * - Seamless switching between creation and edition modes
 * </p>
 * Although marked as “currently obsolete” internally, this controller serves
 * as a compact example of a CRUD form with validation and UI feedback.
 */
public class GenreFormController extends FormController {

    // ─────────────────────────────────────────────────────────────────────────────
    //  FXML references
    // ─────────────────────────────────────────────────────────────────────────────

    @FXML
    private Label formTitle;

    @FXML
    private TextField nameField;

    @FXML
    private Button saveGenreButton;

    @FXML
    private Label nameCounterLabel;

    @FXML
    private ColorPicker colorPicker;

    private Genre genre;

    private final GenreService genreService = AppContextHolder.getBean(GenreService.class);

    private static final int NAME_LIMIT = 100;

    // -------------------------------------------------------------------------
    // INITIALIZATION
    // -------------------------------------------------------------------------

    /**
     * Initializes the form:
     * - Assigns the entity type
     * - Sets default UI state
     * - Attaches validation helpers and character counters
     * - Tracks unsaved changes through {@link CloseRequestHelper}
     */
    @FXML
    public void initialize() {

        entityType = GENRE;

        saveGenreButton.setDefaultButton(true);
        colorPicker.setValue(Color.web(DEFAULT_TAG_COLOR));

        // Track unsaved changes for window-close confirmation
        CloseRequestHelper.setupWithDelay(
                nameField,
                List.of(nameField),
                List.of()
        );

        // Enforce maximum length and update counter
        CharacterLimiter.attachCharacterLimiter(nameField, nameCounterLabel, NAME_LIMIT);
    }

    // -------------------------------------------------------------------------
    // EDIT MODE
    // -------------------------------------------------------------------------

    /**
     * Enables edition mode by loading the existing entity and rebinding
     * the save button to trigger an update instead of a creation.
     */
    @Override
    public void onEdit(Long entityId) {
        super.onEdit(entityId);
        loadGenreData(entityId);
        saveGenreButton.setOnAction(e -> onUpdateGenre());
    }

    // -------------------------------------------------------------------------
    // SAVE NEW GENRE
    // -------------------------------------------------------------------------

    /**
     * Validates and saves a new Genre.
     * Displays appropriate validation messages when needed.
     */
    @FXML
    private void onSaveGenre() {
        String name = nameField.getText().trim();

        String entity = GENRE.getSingular();

        // Validation
        if (name.isEmpty()) {
            AlertManager.handle(entity, "Name", null, ManualErrorType.REQUIRED_ENTRY);
            return;
        }
        if (genreService.existsByName(name)) {
            AlertManager.handle(entity, "Name", name, ManualErrorType.DUPLICATE_ENTRY);
            return;
        }
        if (name.length() > NAME_LIMIT) {
            AlertManager.handle(entity, "Name", String.valueOf(NAME_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }

        // Convert color
        Color color = colorPicker.getValue();
        String hexColor = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        try {
            genreService.createGenre(name, hexColor);

            AlertHelper.showInfo("Success", null, entity + " saved successfully!");
            clearForm();
            colorPicker.setValue(Color.web(DEFAULT_TAG_COLOR));

        } catch (Exception e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    // -------------------------------------------------------------------------
    // UPDATE EXISTING GENRE
    // -------------------------------------------------------------------------

    /**
     * Validates and updates an existing Genre.
     * Closes the form window once the update is successful.
     */
    @FXML
    private void onUpdateGenre() {
        String name = nameField.getText().trim();

        String entity = GENRE.getSingular();

        // Validation
        if (name.isEmpty()) {
            AlertManager.handle(entity, "Name", null, ManualErrorType.REQUIRED_ENTRY);
            return;
        }
        if (genreService.existsByName(name) && !name.equals(genre.getName())) {
            AlertManager.handle(entity, "Name", name, ManualErrorType.DUPLICATE_ENTRY);
            return;
        }
        if (name.length() > NAME_LIMIT) {
            AlertManager.handle(entity, "Name", String.valueOf(NAME_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }

        // Convert color
        String hexColor = colorToHex(colorPicker.getValue());

        try {
            genreService.updateGenre(entityId, name, hexColor);

            AlertHelper.showInfo("Success", null, entity + " updated successfully!");
            clearForm();

            // Close the form window after a successful update
            Stage stage = (Stage) saveGenreButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    /**
     * Clears all input fields and resets the color picker to its default value.
     */
    private void clearForm() {
        nameField.clear();
        colorPicker.setValue(Color.web(DEFAULT_TAG_COLOR));
    }

    /**
     * Loads an existing Genre into the form for edition.
     */
    private void loadGenreData(Long id) {
        genreService.findById(id).ifPresent(genre -> {
            this.genre = genre;
            formTitle.setText("Edit " + genre.getName());
            nameField.setText(genre.getName());
            colorPicker.setValue(Color.web(genre.getColor()));

            saveGenreButton.setText("Update " + genre.getName());
        });
    }

    /**
     * Utility method for converting a JavaFX {@link Color} to a 6-digit hex string.
     */
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}

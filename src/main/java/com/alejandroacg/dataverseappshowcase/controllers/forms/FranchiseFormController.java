package com.alejandroacg.dataverseappshowcase.controllers.forms;

import com.alejandroacg.dataverseappshowcase.config.AppContextHolder;
import com.alejandroacg.dataverseappshowcase.controllers.details.FranchiseDetailsController;
import com.alejandroacg.dataverseappshowcase.models.Franchise;
import com.alejandroacg.dataverseappshowcase.services.FranchiseService;
import com.alejandroacg.dataverseappshowcase.tasks.FxExecutor;
import com.alejandroacg.dataverseappshowcase.utils.ImageStorage;
import com.alejandroacg.dataverseappshowcase.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.alejandroacg.dataverseappshowcase.utils.EntityType.FRANCHISE;

/**
 * Controller responsible for the creation and edition of Franchise entities.
 * <p>
 * This form handles:
 * - Field validation
 * - Image selection, deletion, and persistence
 * - Asynchronous creation and update operations
 * - Synchronization with open details views
 * </p>
 * The form supports two modes:
 * - Creation mode (default)
 * - Edition mode (activated via {@link #onEdit(Long)})
 */
public class FranchiseFormController extends FormController {

    // ─────────────────────────────────────────────────────────────────────────────
    //  FXML references
    // ─────────────────────────────────────────────────────────────────────────────

    @FXML
    private StackPane rootStack;

    @FXML
    private Label formTitle;

    @FXML
    private TextField englishNameField;

    @FXML
    private TextField originalNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private StackPane imageContainer;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Button browse;

    @FXML
    private Button saveFranchiseButton;

    @FXML
    private Label englishNameCounterLabel;

    @FXML
    private Label originalNameCounterLabel;

    @FXML
    private Label descriptionCounterLabel;

    private StackPane overlay;

    // ─────────────────────────────────────────────────────────────────────────────
    //  Internal state and injected services
    // ─────────────────────────────────────────────────────────────────────────────

    private ImageSelector imageSelector;

    private Franchise franchise;

    private final FranchiseService franchiseService = AppContextHolder.getBean(FranchiseService.class);

    // Field length constraints
    final int ENGLISH_NAME_LIMIT = 100;
    final int ORIGINAL_NAME_LIMIT = 100;
    final int DESCRIPTION_LIMIT = 2000;

    // ─────────────────────────────────────────────────────────────────────────────
    //  Initialization
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Initializes the form:
     * - Assigns entity type
     * - Configures image selector utilities
     * - Configures validation helpers and character counters
     */
    @FXML
    public void initialize() {

        entityType = FRANCHISE;

        imageSelector = new ImageSelector(imageContainer, imagePreview);

        saveFranchiseButton.setDefaultButton(true);

        // Warn user before closing if there are unsaved changes
        CloseRequestHelper.setupWithDelay(
                englishNameField,
                List.of(englishNameField, originalNameField, descriptionField),
                List.of(() -> imageSelector.getSelectedImage() != null)
        );

        ClipboardHelper.enablePasteFromClipboard(imageContainer, () -> imageSelector.pasteImageFromClipboard());

        // Character counters
        CharacterLimiter.attachCharacterLimiter(englishNameField, englishNameCounterLabel, ENGLISH_NAME_LIMIT);
        CharacterLimiter.attachCharacterLimiter(originalNameField, originalNameCounterLabel, ORIGINAL_NAME_LIMIT);
        CharacterLimiter.attachCharacterLimiter(descriptionField, descriptionCounterLabel, DESCRIPTION_LIMIT);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Edition mode setup
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Called when the form is opened to edit an existing franchise.
     * Loads entity data and rebinds the save button to perform an update instead of creation.
     */
    @Override
    public void onEdit(Long entityId) {
        super.onEdit(entityId);
        loadFranchiseData(entityId);
        saveFranchiseButton.setOnAction(e -> onUpdateFranchise());
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  UI actions
    // ─────────────────────────────────────────────────────────────────────────────

    @FXML
    private void onBrowseImage() {
        imageSelector.openFileChooser(imagePreview.getScene().getWindow());
    }

    @FXML
    private void onDeleteImage() {
        if (imageSelector.getSelectedImage() != null) {
            AlertHelper.showConfirmation(
                    "Confirm Deletion",
                    null,
                    "Are you sure you want to delete the image?\nThis action cannot be undone.",
                    true
            ).ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    imageSelector.clear(false);
                }
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Creation flow
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Validates and asynchronously creates a new Franchise.
     * Uses {@link FxExecutor} to avoid blocking the UI thread.
     */
    @FXML
    private void onSaveFranchise() {
        String englishName = englishNameField.getText().trim();
        String originalName = originalNameField.getText().trim();
        String desc = descriptionField.getText().trim();
        BufferedImage image = imageSelector.getSelectedImage();

        String entity = FRANCHISE.getSingular();

        // Validation
        if (englishName.isEmpty()) {
            AlertManager.handle(entity, "English Name", null, ManualErrorType.REQUIRED_ENTRY);
            return;
        }
        if (franchiseService.existsByEnglishName(englishName)) {
            AlertManager.handle(entity, "English Name", englishName, ManualErrorType.DUPLICATE_ENTRY);
            return;
        }
        if (englishName.length() > ENGLISH_NAME_LIMIT) {
            AlertManager.handle(entity, "English Name", String.valueOf(ENGLISH_NAME_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }
        if (originalName.isEmpty()) {
            AlertManager.handle(entity, "Original Name", null, ManualErrorType.REQUIRED_ENTRY);
            return;
        }
        if (franchiseService.existsByOriginalName(originalName)) {
            AlertManager.handle(entity, "Original Name", originalName, ManualErrorType.DUPLICATE_ENTRY);
            return;
        }
        if (originalName.length() > ORIGINAL_NAME_LIMIT) {
            AlertManager.handle(entity, "Original Name", String.valueOf(ORIGINAL_NAME_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }
        if (desc.length() > DESCRIPTION_LIMIT) {
            AlertManager.handle(entity, "Description", String.valueOf(DESCRIPTION_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }

        showLoading(true);

        FxExecutor.runAsync(
                () -> {
                    String profileImagePath = null;
                    try {
                        if (image != null) {
                            profileImagePath = ImageStorage.saveImage(image, FRANCHISE.getPluralLowerCase());
                        }

                        franchiseService.createFranchise(
                                englishName,
                                originalName,
                                desc,
                                profileImagePath);

                        return profileImagePath;

                    } catch (Exception e) {
                        cleanUpImageIfNeeded(profileImagePath);
                        GlobalExceptionHandler.handle(e);
                        throw new RuntimeException(e);
                    }
                },
                profileImagePath -> {
                    showLoading(false);
                    AlertHelper.showInfo("Success", null, englishName + " saved successfully!");
                    imageSelector.clear(false);
                    clearForm();
                },
                error -> showLoading(false)
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Update flow
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Performs validation and applies updates to an existing Franchise.
     * Also refreshes the details tab if it is currently open.
     */
    @FXML
    private void onUpdateFranchise() {
        String englishName = englishNameField.getText().trim();
        String originalName = originalNameField.getText().trim();
        String desc = descriptionField.getText().trim();
        BufferedImage image = imageSelector.getSelectedImage();

        String entity = FRANCHISE.getSingular();

        // Same validation logic as creation
        if (englishName.isEmpty()) {
            AlertManager.handle(entity, "English Name", null, ManualErrorType.REQUIRED_ENTRY);
            return;
        }
        if (franchiseService.existsByEnglishName(englishName) && !englishName.equals(franchise.getEnglishName())) {
            AlertManager.handle(entity, "English Name", englishName, ManualErrorType.DUPLICATE_ENTRY);
            return;
        }
        if (englishName.length() > ENGLISH_NAME_LIMIT) {
            AlertManager.handle(entity, "English Name", String.valueOf(ENGLISH_NAME_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }
        if (originalName.isEmpty()) {
            AlertManager.handle(entity, "Original Name", null, ManualErrorType.REQUIRED_ENTRY);
            return;
        }
        if (franchiseService.existsByOriginalName(originalName) && !originalName.equals(franchise.getOriginalName())) {
            AlertManager.handle(entity, "Original Name", originalName, ManualErrorType.DUPLICATE_ENTRY);
            return;
        }
        if (originalName.length() > ORIGINAL_NAME_LIMIT) {
            AlertManager.handle(entity, "Original Name", String.valueOf(ORIGINAL_NAME_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }
        if (desc.length() > DESCRIPTION_LIMIT) {
            AlertManager.handle(entity, "Description", String.valueOf(DESCRIPTION_LIMIT), ManualErrorType.LENGTH_EXCEEDED);
            return;
        }

        showLoading(true);

        FxExecutor.runAsync(
                () -> {
                    String profileImagePath = null;
                    try {
                        if (image != null) {
                            ImageStorage.deleteImageByPath(franchise.getProfileImagePath());
                            profileImagePath = ImageStorage.saveImage(image, FRANCHISE.getPluralLowerCase());
                        }

                        franchiseService.updateFranchise(entityId, englishName, originalName, desc, profileImagePath);
                        return profileImagePath;
                    } catch (Exception e) {
                        cleanUpImageIfNeeded(profileImagePath);
                        GlobalExceptionHandler.handle(e);
                        throw new RuntimeException(e);
                    }
                },
                profileImagePath -> {
                    showLoading(false);
                    AlertHelper.showInfo("Success", null, englishName + " updated successfully!");
                    imageSelector.clear(false);

                    // Update details tab if open
                    String tabId = KeyBuilder.detailsTabKey(FRANCHISE, entityId);

                    FranchiseDetailsController detailsController =
                            TabManager.getInstance().getControllerForTab(tabId, FranchiseDetailsController.class);

                    if (detailsController != null) {
                        Franchise updated = franchiseService.findById(franchise.getId()).orElseThrow();
                        detailsController.setFranchise(updated);
                    }

                    // Close the edit window
                    Stage stage = (Stage) saveFranchiseButton.getScene().getWindow();
                    stage.close();
                },
                error -> showLoading(false)
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Deletes a saved image if an operation fails after persisting it.
     */
    private void cleanUpImageIfNeeded(String imagePath) {
        if (imagePath != null) {
            try {
                ImageStorage.deleteImageByPath(imagePath);
            } catch (Exception ex) {
                GlobalExceptionHandler.handle(ex);
            }
        }
    }

    /** Resets the form to its default empty state. */
    private void clearForm() {
        englishNameField.clear();
        originalNameField.clear();
        descriptionField.clear();
        imagePreview.setImage(null);
    }

    /**
     * Loads an existing Franchise into the form for edition.
     */
    private void loadFranchiseData(Long id) {
        franchiseService.findById(id).ifPresent(franchise -> {
            this.franchise = franchise;
            formTitle.setText("Edit " + franchise.getEnglishName());
            englishNameField.setText(franchise.getEnglishName());
            originalNameField.setText(franchise.getOriginalName());
            descriptionField.setText(franchise.getDescription());
            if (franchise.getProfileImagePath() != null) {
                imageSelector.loadImageFromPath(franchise.getProfileImagePath());
            }
            saveFranchiseButton.setText("Update " + franchise.getEnglishName());
        });
    }

    /**
     * Shows or hides the loading overlay for asynchronous operations.
     */
    private void showLoading(boolean show) {
        if (overlay != null) {
            overlay.setVisible(show);
            overlay.setManaged(show);
        }
    }
}

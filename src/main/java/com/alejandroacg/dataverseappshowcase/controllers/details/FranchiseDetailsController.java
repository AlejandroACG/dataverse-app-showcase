package com.alejandroacg.dataverseappshowcase.controllers.details;

import com.alejandroacg.dataverseappshowcase.config.AppContextHolder;
import com.alejandroacg.dataverseappshowcase.models.Franchise;
import com.alejandroacg.dataverseappshowcase.services.FranchiseService;
import com.alejandroacg.dataverseappshowcase.tasks.FxExecutor;
import com.alejandroacg.dataverseappshowcase.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.BIG_FORM_HEIGHT;
import static com.alejandroacg.dataverseappshowcase.config.UIConstants.BIG_FORM_WIDTH;
import static com.alejandroacg.dataverseappshowcase.utils.EntityType.FRANCHISE;

/**
 * Controller responsible for rendering the detailed view of a Franchise.
 * <p>
 * This class loads the selected franchise asynchronously, updates the UI
 * with its information, and provides Edit/Delete actions. It also manages
 * a loading overlay to avoid UI blocking during database operations.
 * </p>
 */
public class FranchiseDetailsController extends DetailsController {

    /** Root layout container for the details view. */
    @FXML
    private BorderPane detailsPane;

    /** Displays the franchise's image. */
    @FXML
    private ImageView franchiseImage;

    /** Displays the English name of the franchise. */
    @FXML
    private Label englishNameLabel;

    /** Displays the original name of the franchise. */
    @FXML
    private Label originalNameLabel;

    /** Displays the franchise's description text. */
    @FXML
    private Label descriptionLabel;

    /** Overlay displayed during asynchronous loading operations. */
    private StackPane overlay;

    /** The entity type associated with this details screen. */
    @Getter
    private final EntityType entityType = FRANCHISE;

    /** Service used to retrieve and manipulate Franchise records. */
    private final FranchiseService franchiseService =
            AppContextHolder.getBean(FranchiseService.class);

    /** Currently loaded franchise instance. */
    private Franchise franchise;

    // -------------------------------------------------------------------------
    // INITIALIZATION
    // -------------------------------------------------------------------------

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     * Initializes and installs the loading overlay.
     */
    @FXML
    private void initialize() {
        setupOverlay();
    }

    /**
     * Loads the reusable loading overlay and places it on top of the view's center content.
     * The overlay remains hidden until a long-running operation begins.
     */
    private void setupOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loading_overlay.fxml"));
            overlay = loader.load();
            overlay.setVisible(false);
            overlay.setManaged(false);

            // Wrap the current center content with the overlay
            Parent centerContent = (Parent) detailsPane.getCenter();
            StackPane stack = new StackPane(centerContent, overlay);
            detailsPane.setCenter(stack);

        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    // -------------------------------------------------------------------------
    // ENTITY ASSIGNMENT
    // -------------------------------------------------------------------------

    /**
     * Assigns the ID of the entity to load and triggers an asynchronous fetch from the database.
     */
    @Override
    public void setEntityId(Long entityId) {
        super.setEntityId(entityId);
        loadFranchiseAsync(entityId);
    }

    // -------------------------------------------------------------------------
    // ASYNC LOADING
    // -------------------------------------------------------------------------

    /**
     * Loads franchise data from the database on a background thread.
     * Displays the loading overlay while the operation is running.
     */
    private void loadFranchiseAsync(Long id) {
        showLoading(true);

        FxExecutor.runAsync(
                () -> franchiseService.findById(id).orElse(null),

                result -> {
                    showLoading(false);
                    if (result == null) {
                        AlertHelper.showError("Error", null, "Could not load franchise.");
                        closeThisTab();
                        return;
                    }
                    setFranchise(result);
                },

                error -> {
                    showLoading(false);
                    AlertHelper.showError("Error", null, "Could not load franchise.");
                    closeThisTab();
                }
        );
    }

    /**
     * Requests that the tab displaying this controller be closed.
     */
    private void closeThisTab() {
        TabManager manager = TabManager.getInstance();
        manager.closeTabByController(this);
    }

    // -------------------------------------------------------------------------
    // UI UPDATE
    // -------------------------------------------------------------------------

    /**
     * Populates the UI fields with data from the loaded Franchise.
     */
    public void setFranchise(Franchise franchise) {
        this.franchise = franchise;

        englishNameLabel.setText(franchise.getEnglishName());
        LabelUtils.makeLabelCopyable(englishNameLabel);

        originalNameLabel.setText(franchise.getOriginalName());
        LabelUtils.makeLabelCopyable(originalNameLabel);

        descriptionLabel.setText(franchise.getDescription());
        LabelUtils.makeLabelCopyable(descriptionLabel);

        loadImage(franchise.getProfileImagePath());
    }

    /**
     * Loads the franchise's image from disk and renders it in the UI.
     * If the image is missing or invalid, nothing is displayed.
     */
    private void loadImage(String path) {
        if (path == null) return;

        File file = new File(path);
        if (!file.exists()) return;

        Image img = new Image(file.toURI().toString());
        franchiseImage.setImage(img);

        double scale = 200.0 / img.getWidth();
        franchiseImage.setFitHeight(img.getHeight() * scale);
    }

    // -------------------------------------------------------------------------
    // ACTIONS
    // -------------------------------------------------------------------------

    /**
     * Opens the edit window for the current franchise.
     */
    @FXML
    private void onEditFranchise() {
        if (entityId != null && franchise != null) {
            WindowHelper.openEditWindow(
                    FRANCHISE,
                    "Edit " + franchise.getEnglishName(),
                    BIG_FORM_WIDTH,
                    BIG_FORM_HEIGHT,
                    entityId
            );
        }
    }

    /**
     * Prompts for confirmation and deletes the current franchise if accepted.
     * <p>
     * TODO: After deletion completes, this tab should close automatically and
     *       the search screen should avoid opening or referencing deleted entities.
     * </p>
     */
    @FXML
    private void onDeleteFranchise() {
        if (entityId == null || franchise == null) return;

        AlertHelper.showConfirmation(
                "Confirm Deletion",
                null,
                "Are you sure you want to delete " + franchise.getEnglishName() +
                        "?\nThis action cannot be undone.",
                true
        ).ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                franchiseService.deleteFranchise(franchise.getId());
            }
        });
    }

    // -------------------------------------------------------------------------
    // OVERLAY CONTROL
    // -------------------------------------------------------------------------

    /**
     * Shows or hides the loading overlay.
     */
    private void showLoading(boolean show) {
        if (overlay != null) {
            overlay.setVisible(show);
            overlay.setManaged(show);
        }
    }
}

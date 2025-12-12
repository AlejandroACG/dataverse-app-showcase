package com.alejandroacg.dataverseappshowcase.utils;

import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.Getter;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.IMAGE_SELECTOR_HEIGHT;
import static com.alejandroacg.dataverseappshowcase.config.UIConstants.IMAGE_SELECTOR_WIDTH;
import static com.alejandroacg.dataverseappshowcase.utils.ImageHelper.convertToFxImage;
import static com.alejandroacg.dataverseappshowcase.utils.ImageHelper.loadImageIntoView;

/**
 * Utility component responsible for selecting, previewing, and keeping an image
 * in memory for entity forms.
 *
 * <p>This class encapsulates all common image-selection workflows:
 * <ul>
 *     <li>Loading images via file chooser</li>
 *     <li>Handling drag-and-drop of image files</li>
 *     <li>Pasting images directly from the system clipboard</li>
 *     <li>Remembering the last-used directory for convenience</li>
 * </ul>
 *
 * It exposes the selected image as a {@link BufferedImage} so backend services
 * may persist or transform it as needed.
 */
public class ImageSelector {

    /** Path to a simple local configuration file storing the last-used directory. */
    private static final String CONFIG_FILE = "config/images.properties";

    /** Property key for persisting the last directory accessed by the user. */
    private static final String LAST_PATH_KEY = "lastImagePath";

    /** Cached last-used directory (loaded lazily on first use). */
    private static String lastUsedPath = null;

    /** The UI container where drag-and-drop is enabled and the preview lives. */
    private final StackPane container;

    /** Preview component used to display the currently selected image. */
    private final ImageView preview;

    /**
     * The image currently selected by the user.
     * Stored as AWT {@link BufferedImage} so it can be saved using {@link ImageStorage}.
     */
    @Getter
    private BufferedImage selectedImage;

    /**
     * Creates a new selector bound to the given UI container and preview component.
     * Immediately initializes drag-and-drop support.
     */
    public ImageSelector(StackPane container, ImageView preview) {
        this.container = container;
        this.preview = preview;

        setupDragAndDrop();
    }

    /**
     * Opens a file chooser dialog to allow the user to select an image from disk.
     * The last directory used by the user is restored to streamline the workflow.
     *
     * @param owner the window that owns the file chooser (typically the form stage)
     */
    public void openFileChooser(Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        // Load lazily the last-used directory
        if (lastUsedPath == null) {
            loadLastUsedPath();
        }

        if (lastUsedPath != null) {
            File lastDir = new File(lastUsedPath);
            if (lastDir.exists()) {
                fc.setInitialDirectory(lastDir);
            }
        }

        File file = fc.showOpenDialog(owner);
        if (file != null && isImageFile(file)) {
            saveLastUsedPath(file.getParent());
            setImage(file);
        }
    }

    /**
     * Clears the selected image and optionally hides the preview container.
     *
     * @param disappear if true, the preview container is hidden entirely; otherwise the preview is simply cleared
     */
    public void clear(Boolean disappear) {
        preview.setImage(null);
        if (disappear) {
            container.setVisible(false);
            container.setManaged(false);
        } else {
            preview.setFitWidth(0);
            preview.setFitHeight(0);
        }
        selectedImage = null;
    }

    /**
     * Attempts to read an image from the system clipboard and display it.
     * Supports native image clipboard formats. Non-buffered images are converted.
     */
    public void pasteImageFromClipboard() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
            try {
                Image awtImage = (Image) clipboard.getData(DataFlavor.imageFlavor);

                // Always store internally as BufferedImage
                if (awtImage instanceof BufferedImage bufferedImage) {
                    this.selectedImage = bufferedImage;
                } else {
                    BufferedImage converted = new BufferedImage(
                            awtImage.getWidth(null),
                            awtImage.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    Graphics2D g = converted.createGraphics();
                    g.drawImage(awtImage, 0, 0, null);
                    g.dispose();
                    this.selectedImage = converted;
                }

                // Render JavaFX preview
                javafx.scene.image.Image fxImage = convertToFxImage(this.selectedImage);
                preview.setImage(fxImage);
                preview.setFitWidth(Math.min(fxImage.getWidth(), 400));
                preview.setPreserveRatio(true);
                container.setVisible(true);
                container.setManaged(true);

            } catch (Exception e) {
                GlobalExceptionHandler.handle(e);
            }
        }
    }

    // =====================================================================
    //  Internal Helper Logic
    // =====================================================================

    /**
     * Enables drag-and-drop support for image files on the container.
     */
    private void setupDragAndDrop() {
        container.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() && db.getFiles().size() == 1 && isImageFile(db.getFiles().getFirst())) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        container.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() && db.getFiles().size() == 1) {
                File file = db.getFiles().getFirst();
                if (isImageFile(file)) {
                    setImage(file);
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    /**
     * Loads the given file into memory and updates the JavaFX preview.
     * The loaded image becomes the currently selected image.
     */
    private void setImage(File file) {
        this.selectedImage = loadImageIntoView(file, preview, container, IMAGE_SELECTOR_WIDTH, IMAGE_SELECTOR_HEIGHT);
    }

    /**
     * Validates that the file extension corresponds to a supported image format.
     */
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg")
                || name.endsWith(".jpeg") || name.endsWith(".webp");
    }

    /**
     * Loads the last-used directory from disk into memory.
     * Called lazily when first needed.
     */
    private static void loadLastUsedPath() {
        Properties props = new Properties();
        try (var in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
            lastUsedPath = props.getProperty(LAST_PATH_KEY);
        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    /**
     * Persists the most recently used directory so future file-chooser dialogs
     * start in that location.
     */
    private static void saveLastUsedPath(String path) {
        Properties props = new Properties();
        props.setProperty(LAST_PATH_KEY, path);
        try {
            File configFile = new File(CONFIG_FILE);
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream out = new FileOutputStream(configFile)) {
                props.store(out, null);
            }

            lastUsedPath = path;

        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    /**
     * Attempts to load an image from an absolute path already stored in the model.
     * Used when opening forms in edit mode with previously saved images.
     *
     * @param path filesystem path to an image
     */
    public void loadImageFromPath(String path) {
        if (path == null || path.isBlank()) return;

        File file = new File(path);
        if (file.exists() && isImageFile(file)) {
            setImage(file);
        } else {
            GlobalExceptionHandler.handle(new IOException("Image file does not exist or is invalid: " + path));
        }
    }
}

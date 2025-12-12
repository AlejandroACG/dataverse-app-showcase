package com.alejandroacg.dataverseappshowcase.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility class providing helper methods for loading and converting images.
 *
 * <p>Its responsibilities are strictly limited to:
 * <ul>
 *     <li>Reading an image file into a {@link BufferedImage}</li>
 *     <li>Converting AWT {@link BufferedImage} to JavaFX {@link javafx.scene.image.Image}</li>
 *     <li>Rendering and scaling images into UI components such as {@link ImageView}</li>
 * </ul>
 *
 * <p>It does not perform any disk persistence or image transformation beyond scaling.
 * Those concerns are handled by {@link ImageStorage} and {@link ImageSelector}.
 */
public class ImageHelper {

    /**
     * Loads an image from disk, converts it to a JavaFX-compatible format,
     * displays it inside the given {@link ImageView}, and returns the underlying
     * {@link BufferedImage} for backend use.
     *
     * <p>This method:
     * <ul>
     *     <li>Reads the file using {@link ImageIO}</li>
     *     <li>Converts the result to a JavaFX image</li>
     *     <li>Calculates a scale factor to fit the provided max width/height</li>
     *     <li>Ensures that the container (wrapper) becomes visible</li>
     * </ul>
     *
     * @param file       the image file to load
     * @param imageView  the JavaFX {@link ImageView} where the image should be rendered
     * @param wrapper    a container that should become visible once an image is loaded
     * @param maxWidth   maximum width allowed for the preview
     * @param maxHeight  maximum height allowed for the preview
     * @return the loaded {@link BufferedImage}, or {@code null} if loading fails
     */
    public static BufferedImage loadImageIntoView(
            File file, ImageView imageView, StackPane wrapper, double maxWidth, double maxHeight) {
        try {
            BufferedImage awtImage = ImageIO.read(file);
            if (awtImage == null) {
                throw new IOException("Unsupported or unreadable image format: " + file.getName());
            }

            // Convert AWT image into a JavaFX image
            javafx.scene.image.Image fxImage = convertToFxImage(awtImage);

            // Configure the ImageView
            imageView.setImage(fxImage);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // Scale proportionally to fit the preview dimensions
            double scaleFactor = Math.min(maxWidth / fxImage.getWidth(), maxHeight / fxImage.getHeight());

            imageView.setFitWidth(fxImage.getWidth() * scaleFactor);
            imageView.setFitHeight(fxImage.getHeight() * scaleFactor);

            // Ensure wrapper visibility
            if (wrapper != null) {
                wrapper.setVisible(true);
                wrapper.setManaged(true);
                wrapper.setStyle("-fx-background-color: transparent;");
            }

            return awtImage;

        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
            return null;
        }
    }

    /**
     * Converts an AWT {@link BufferedImage} into a JavaFX {@link javafx.scene.image.Image}.
     *
     * <p>This is required because JavaFX cannot directly render AWT images,
     * and Swing provides no native support for JavaFX components.
     *
     * @param bufferedImage the AWT image to convert
     * @return a JavaFX {@link javafx.scene.image.Image} containing the same pixel data
     */
    public static javafx.scene.image.Image convertToFxImage(BufferedImage bufferedImage) {
        WritableImage fxImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        SwingFXUtils.toFXImage(bufferedImage, fxImage);
        return fxImage;
    }
}

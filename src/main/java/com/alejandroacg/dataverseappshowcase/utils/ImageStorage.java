package com.alejandroacg.dataverseappshowcase.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Utility class responsible for persisting and deleting image files
 * associated with application entities.
 *
 * <p>The class abstracts all disk-level operations related to image storage,
 * including directory creation, unique file naming, and safe deletion.
 * All stored images are saved under a structured root directory:
 *
 * <pre>
 *     data/images/{entityType}/generated_file.png
 * </pre>
 *
 * <p>Any I/O failures while saving images are surfaced through
 * {@link AlertManager} to provide immediate user feedback.
 */
public class ImageStorage {

    /** Base directory used for all stored entity images. */
    private static final String BASE_DIR = "data/images/";

    /**
     * Saves a {@link BufferedImage} to disk inside a folder associated with the
     * given entity type. The method guarantees a unique filename using a UUID
     * and automatically creates required directories if they do not exist.
     *
     * <p>Example resulting path: {@code data/images/franchises/uuid.png}</p>
     *
     * @param image      the image to be written to disk
     * @param entityType logical grouping (e.g., "franchises", "genres")
     * @return a normalized string path to the stored image, or {@code null} if the save failed
     */
    public static String saveImage(BufferedImage image, String entityType) {
        try {
            Path dir = Path.of(BASE_DIR, entityType);
            Files.createDirectories(dir);

            String fileName;
            Path target;

            // Generate a guaranteed unique filename
            do {
                String uuid = UUID.randomUUID().toString();
                fileName = uuid + ".png";
                target = dir.resolve(fileName);
            } while (Files.exists(target));

            ImageIO.write(image, "png", target.toFile());

            // Normalize path separators for cross-platform consistency
            return target.toString().replace("\\", "/");

        } catch (IOException e) {
            // Surface a user-friendly error; logging is delegated to AlertManager
            AlertManager.handle("Franchise", null, null, ManualErrorType.STORAGE_IN_ERROR);
            return null;
        }
    }

    /**
     * Deletes an image file from disk if the provided path is valid and the file exists.
     *
     * <p>This method does not produce user-facing alerts because deletion failures
     * are typically non-critical (e.g., missing file on update). The caller may
     * choose to handle exceptions where appropriate.</p>
     *
     * @param imagePath path to the file on disk
     * @throws IOException if deletion fails unexpectedly
     */
    public static void deleteImageByPath(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isBlank()) return;

        Path path = Path.of(imagePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}

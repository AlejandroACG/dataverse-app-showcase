package com.alejandroacg.dataverseappshowcase.utils;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Utility class providing quality-of-life enhancements for JavaFX {@link Label} components.
 *
 * <p>The primary feature implemented here is enabling labels to behave as
 * copy-to-clipboard fields, which improves usability in details views where
 * users may frequently copy identifiers, names, or descriptions.
 *
 * <p>This class does not modify text content, formatting, or styling beyond
 * optional cursor and tooltip hints.
 */
public class LabelUtils {

    /**
     * Enhances a {@link Label} so that clicking it will copy its text content
     * to the system clipboard.
     *
     * <p>The method applies:
     * <ul>
     *     <li>A hand cursor to indicate interactivity</li>
     *     <li>A tooltip explaining the action</li>
     *     <li>A mouse click listener that delegates to {@link #copyLabelToClipboard(Label)}</li>
     * </ul>
     *
     * @param label the label that should receive copy-on-click behavior
     */
    public static void makeLabelCopyable(Label label) {
        label.setCursor(Cursor.HAND);
        label.setTooltip(new Tooltip("Click to copy"));
        label.setOnMouseClicked(event -> copyLabelToClipboard(label));
    }

    /**
     * Copies the text of the given {@link Label} into the system clipboard.
     *
     * <p>No formatting or trimming is applied; the clipboard receives the exact
     * character sequence stored in {@link Label#getText()}.
     *
     * @param label the label whose text will be copied
     */
    public static void copyLabelToClipboard(Label label) {
        String text = label.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}

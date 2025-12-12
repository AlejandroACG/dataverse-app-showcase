package com.alejandroacg.dataverseappshowcase.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

/**
 * Utility class that enforces a maximum character length on {@link TextInputControl}
 * components such as {@link javafx.scene.control.TextField} and {@link javafx.scene.control.TextArea}.
 *
 * <p>When the configured limit is exceeded, the input is rolled back to its previous
 * value. A counter label is updated in real time to reflect the current number of
 * characters entered.</p>
 *
 * <p><strong>Current limitation:</strong> Pasting content that exceeds the limit will
 * revert the field entirely to its old value, instead of truncating the input. In
 * multi-line components like {@link javafx.scene.control.TextArea}, this also causes
 * the caret to jump to the beginning of the text. This behavior is documented in the
 * TODO inside the class.</p>
 */
public class CharacterLimiter {

    /**
     * Attaches a character limit to the given text input control and updates a counter label
     * to display {@code currentLength / maxLength}.
     *
     * <p>If the user attempts to enter or paste text exceeding the limit, the value is
     * reverted to its previous valid state.</p>
     *
     * @param field        the input component whose text will be limited
     * @param counterLabel the label displaying the character count
     * @param maxLength    the maximum number of characters allowed
     */
    public static void attachCharacterLimiter(TextInputControl field, Label counterLabel, int maxLength) {

        // TODO It entirely stops pasting if it surpasses the limit, instead of cutting it.
        //  In the case of TextArea it also sends the user to the start.
        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > maxLength) {
                field.setText(oldText);
            } else {
                counterLabel.setText(newText.length() + " / " + maxLength);
            }
        });
    }
}

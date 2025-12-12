package com.alejandroacg.dataverseappshowcase.controllers;

import com.alejandroacg.dataverseappshowcase.utils.EntityType;
import com.alejandroacg.dataverseappshowcase.utils.WindowHelper;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.*;
import static com.alejandroacg.dataverseappshowcase.utils.EntityType.*;

public class NewMenuController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private void onOpenFranchiseForm() {
        openForm(FRANCHISE, BIG_FORM_WIDTH, BIG_FORM_HEIGHT);
    }

    @FXML
    private void onOpenGenreForm() {
        openForm(GENRE, SMALL_FORM_WIDTH, SMALL_FORM_HEIGHT);
    }

    private void openForm(EntityType type, int width, int height) {
        WindowHelper.openWindow(type, "New " + type.getSingular(), width, height);
    }
}

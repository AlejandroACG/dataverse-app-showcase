package com.alejandroacg.dataverseappshowcase.controllers;

import com.alejandroacg.dataverseappshowcase.utils.EntityType;
import com.alejandroacg.dataverseappshowcase.utils.KeyBuilder;
import com.alejandroacg.dataverseappshowcase.utils.TabManager;
import com.alejandroacg.dataverseappshowcase.utils.WindowHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.MENU_WINDOW_HEIGHT;
import static com.alejandroacg.dataverseappshowcase.config.UIConstants.MENU_WINDOW_WIDTH;
import static com.alejandroacg.dataverseappshowcase.utils.CloseRequestHelper.tryCloseAll;

public class MainController {

    @FXML
    private TabManager tabManager;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button newButton;

    @FXML
    private Button searchFranchisesButton;

    @FXML
    private Button searchGenresButton;

    @FXML
    private ScrollPane navScrollPane;

    @FXML
    public void initialize() {
        setupWindowCloseHandling();
        tabManager = new TabManager(tabPane);
        TabManager.setInstance(tabManager);
    }

    private void setupWindowCloseHandling() {
        Platform.runLater(() -> tabPane.getScene().getWindow().setOnCloseRequest(e -> {
            if (!tryCloseAll(WindowHelper.getOpenWindows())) {
                e.consume();
            }
        }));
    }

    @FXML
    private void onNewMenu() {
        WindowHelper.openWindow(
                KeyBuilder.windowNewMenuKey(),
                KeyBuilder.newMenuFXMLPath(),
                "New",
                MENU_WINDOW_WIDTH,
                MENU_WINDOW_HEIGHT
        );
    }

    @FXML
    private void onSearchFranchises() {
        tabManager.openSearchTab(EntityType.FRANCHISE);
    }

    @FXML
    private void onSearchGenres() {
        tabManager.openSearchTab(EntityType.GENRE);
    }
}

package com.alejandroacg.dataverseappshowcase.controllers;

import com.alejandroacg.dataverseappshowcase.config.AppContextHolder;
import com.alejandroacg.dataverseappshowcase.models.Franchise;
import com.alejandroacg.dataverseappshowcase.models.Genre;
import com.alejandroacg.dataverseappshowcase.search.SearchBouncer;
import com.alejandroacg.dataverseappshowcase.search.SearchHandler;
import com.alejandroacg.dataverseappshowcase.search.SearchResult;
import com.alejandroacg.dataverseappshowcase.services.GenreService;
import com.alejandroacg.dataverseappshowcase.tasks.FxExecutor;
import com.alejandroacg.dataverseappshowcase.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.alejandroacg.dataverseappshowcase.config.UIConstants.*;
import static com.alejandroacg.dataverseappshowcase.utils.EntityType.FRANCHISE;
import static com.alejandroacg.dataverseappshowcase.utils.EntityType.GENRE;
import static com.alejandroacg.dataverseappshowcase.utils.ImageHelper.loadImageIntoView;
import static com.alejandroacg.dataverseappshowcase.utils.ManualErrorType.STORAGE_OUT_ERROR;

public class SearchController {

    private final Debouncer searchDebouncer =
            new Debouncer(250, this::performSearchAsync);

    private final SearchBouncer bouncer = new SearchBouncer();

    @FXML
    private TilePane resultsPane;

    @Getter
    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox filterBox;

    @FXML
    private HBox paginationBox;

    @FXML
    private StackPane centerStack;

    private StackPane overlay;

    @Getter
    private SearchHandler searchHandler;

    @Getter
    @Setter
    private EntityType entityType;

    private final GenreService genreService = AppContextHolder.getBean(GenreService.class);

    private int currentPage = 0;

    private int totalPages = 1;

    // -------------------------------------------------------------------------
    //  INITIALIZATION
    // -------------------------------------------------------------------------

    public void setSearchHandler(SearchHandler handler) {
        this.searchHandler = handler;
        handler.setController(this);
        filterBox.getChildren().add(handler.getSearchFilters());
        setupOverlay();
        performSearchAsync();
    }

    private void setupOverlay() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loading_overlay.fxml"));
            overlay = loader.load();
            overlay.setVisible(false);
            overlay.setManaged(false);

            centerStack.getChildren().add(overlay);

        } catch (IOException e) {
            GlobalExceptionHandler.handle(e);
        }
    }

    // -------------------------------------------------------------------------
    //  PUBLIC API
    // -------------------------------------------------------------------------

    public void triggerSearch() {
        currentPage = 0;
        searchDebouncer.trigger();
    }

    public void goToNextPage() {
        if (currentPage + 1 < totalPages) {
            currentPage++;
            performSearchAsync();
        }
    }

    public void goToPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            performSearchAsync();
        }
    }

    // -------------------------------------------------------------------------
    //  ASYNC SEARCH
    // -------------------------------------------------------------------------

    private void performSearchAsync() {
        long requestId = bouncer.nextRequestId();
        int page = currentPage;
        int pageSize;
        if (entityType == FRANCHISE) {
            pageSize = PAGE_SIZE_BIG_CARDS;
        } else {
            pageSize = PAGE_SIZE_SMALL_CARDS;
        }

        showLoading(true);

        FxExecutor.runAsync(
                () -> {
                    var result = searchHandler.performSearch(page, pageSize);
                    var cards = buildCardsAsync(result.getItems());
                    return new SearchRenderBundle(result, cards);
                },
                result -> {
                    if (!bouncer.isLatest(requestId)) {
                        showLoading(false);
                        return;
                    }

                    SearchResult<?> data = result.result();
                    List<Node> cards = result.cards();

                    this.currentPage = data.getPage();
                    this.totalPages = Math.max(data.getTotalPages(), 1);

                    renderCards(cards);
                    updatePaginationControls();

                    showLoading(false);

                }, error -> {
                    showLoading(false);
                    GlobalExceptionHandler.handle(error);
                }
        );
    }

    private void showLoading(boolean show) {
        if (overlay != null) {
            overlay.setVisible(show);
            overlay.setManaged(show);
        }
        paginationBox.setDisable(show);
    }

    // -------------------------------------------------------------------------
    //  CARD CREATORS
    // -------------------------------------------------------------------------

    private <T> List<Node> buildCardsAsync(List<T> results) {
        return results.stream()
                .map(item -> {
                    Node card = null;
                    if (item instanceof Franchise f) card = createFranchiseCard(f);
                    else if (item instanceof Genre g) card = createGenreCard(g);
                    return card;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private record SearchRenderBundle(SearchResult<?> result, List<Node> cards) {}

    private void renderCards(List<Node> cards) {
        resultsPane.getChildren().setAll(cards);
    }

    private Button createFranchiseCard(Franchise franchise) {
        ImageView imageView = new ImageView();

        StackPane imageWrapper = new StackPane(imageView);
        imageWrapper.setPrefSize(CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT);
        imageWrapper.setMaxSize(CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT);
        imageWrapper.setMinSize(CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT);
        imageWrapper.setStyle("-fx-background-color: transparent; -fx-alignment: center;");

        String imagePath = franchise.getProfileImagePath();
        if (imagePath != null && !imagePath.isBlank()) {
            File file = new File(imagePath);
            if (file.exists()) {
                loadImageIntoView(file, imageView, imageWrapper, CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT);
            } else {
                AlertManager.handle(null, "Image", imagePath, STORAGE_OUT_ERROR);
            }
        }

        Label englishName = new Label(franchise.getEnglishName());
        englishName.setStyle("-fx-font-weight: bold;");
        englishName.setTooltip(new Tooltip(englishName.getText()));

        Label originalName = new Label(franchise.getOriginalName());
        originalName.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
        originalName.setTooltip(new Tooltip(originalName.getText()));

        VBox content = new VBox(5, imageWrapper, englishName, originalName);
        content.setPrefSize(160, 130);
        content.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        content.setStyle("-fx-alignment: center;");

        Button cardButton = new Button();
        cardButton.setGraphic(content);
        cardButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        cardButton.setOnAction(
                e -> TabManager.getInstance().openDetailsTab(
                        FRANCHISE, franchise.getId(), franchise.getEnglishName(), true));

        cardButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                TabManager.getInstance().openDetailsTab(
                        FRANCHISE, franchise.getId(), franchise.getEnglishName(), false);
                e.consume();
            }
        });

        return cardButton;
    }

    private StackPane createGenreCard(Genre genre) {
        Label name = new Label(genre.getName());
        name.setStyle("-fx-font-weight: bold;");
        name.setTooltip(new Tooltip(name.getText()));

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            WindowHelper.openEditWindow(
                    GENRE, "Edit " + GENRE.getSingular(), SMALL_FORM_WIDTH, SMALL_FORM_HEIGHT, genre.getId());
        });

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("danger");
        deleteButton.setOnAction(e -> {
            AlertHelper.showConfirmation(
                    "Confirm Deletion",
                    null,
                    "Are you sure you want to delete " + genre.getName() + "?\nThis action cannot be undone.",
                    true
            ).ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    genreService.deleteGenre(genre.getId());
                    TabManager.getInstance().refreshSearchTab(GENRE);
                }
            });
        });

        HBox buttons = new HBox(4, editButton, deleteButton);
        VBox content = new VBox(10, name, buttons);
        content.setMinSize(155, 100);
        content.setPrefSize(155, 100);
        content.setMaxSize(155, 100);

        StackPane container = new StackPane(content);
        container.setPrefSize(155, 100);
        container.setMaxSize(155, 100);

        String color = genre.getColor();
        if (color != null && !color.isBlank()) {
            content.setStyle("-fx-background-color: " + color + "; -fx-alignment: center;");
            container.setStyle("-fx-background-color: " + color + ";");
        }
        container.getStyleClass().add("tag-container");

        return container;
    }

    private void updatePaginationControls() {
        paginationBox.getChildren().clear();

        if (totalPages <= 1) return;

        // Previous Button
        Button previous = new Button("<");
        previous.setDisable(currentPage == 0);
        previous.setOnAction(e -> goToPreviousPage());
        paginationBox.getChildren().add(previous);

        // Numerical Buttons
        int maxButtons = 7;
        int start = Math.max(0, currentPage - 3);
        int end = Math.min(totalPages - 1, start + maxButtons - 1);

        for (int page = start; page <= end; page++) {
            int pageIndex = page;

            Button pageButton = new Button(String.valueOf(page + 1));

            if (pageIndex == currentPage) {
                pageButton.setStyle("-fx-font-weight: bold; -fx-background-color: #555; -fx-text-fill: white;");
            }

            pageButton.setOnAction(e -> {
                currentPage = pageIndex;
                performSearchAsync();
            });

            paginationBox.getChildren().add(pageButton);
        }

        // Next Button
        Button next = new Button(">");
        next.setDisable(currentPage + 1 >= totalPages);
        next.setOnAction(e -> goToNextPage());
        paginationBox.getChildren().add(next);
    }
}

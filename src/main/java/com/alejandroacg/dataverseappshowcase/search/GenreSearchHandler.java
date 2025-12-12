package com.alejandroacg.dataverseappshowcase.search;

import com.alejandroacg.dataverseappshowcase.config.AppContextHolder;
import com.alejandroacg.dataverseappshowcase.controllers.SearchController;
import com.alejandroacg.dataverseappshowcase.models.Genre;
import com.alejandroacg.dataverseappshowcase.services.GenreService;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Search handler responsible for retrieving paginated {@link Genre} results.
 *
 * <p>This class acts as the bridge between the UI-level {@link SearchController}
 * and the persistence layer ({@link GenreService}). It contains only search-related
 * behavior and no UI rendering logic.</p>
 *
 * <p>For now, this handler exposes no filtering UI and simply returns an empty
 * placeholder node. Additional filters (e.g., by color or alphabetical order)
 * can be added here in the future.</p>
 */
public class GenreSearchHandler implements SearchHandler {

    /** Service providing database access for Genre entities. */
    private final GenreService genreService =
            AppContextHolder.getBean(GenreService.class);

    /** Back-reference to the controller that owns this handler. */
    @Setter
    private SearchController controller;

    /**
     * Returns the UI component containing search filters.
     *
     * <p>Genres currently have no filter options, so a placeholder label
     * is returned. This can later be replaced with filter controls.</p>
     */
    @Override
    public Node getSearchFilters() {
        return new Label();
    }

    /**
     * Performs a paginated search for {@link Genre} entities.
     *
     * <p>Delegates to {@link com.alejandroacg.dataverseappshowcase.services.GenreService#getAllGenresPage} and
     * adapts the result to a {@link SearchResult} DTO for consumption by the UI.</p>
     *
     * @param page zero-based page index
     * @param size number of items per page
     * @return a wrapped, immutable search result structure
     */
    @Override
    public SearchResult<Genre> performSearch(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Genre> resultPage = genreService.getAllGenresPage(pageable);

        return new SearchResult<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );
    }
}

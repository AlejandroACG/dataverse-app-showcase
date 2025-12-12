package com.alejandroacg.dataverseappshowcase.search;

import com.alejandroacg.dataverseappshowcase.config.AppContextHolder;
import com.alejandroacg.dataverseappshowcase.controllers.SearchController;
import com.alejandroacg.dataverseappshowcase.models.Franchise;
import com.alejandroacg.dataverseappshowcase.services.FranchiseService;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * SearchHandler implementation responsible for executing paginated
 * search operations on {@link Franchise} entities.
 *
 * <p>This handler provides:
 * <ul>
 *     <li>UI components for franchise-specific search filters (currently a placeholder).</li>
 *     <li>Paginated retrieval of franchise data via {@link FranchiseService}.</li>
 *     <li>Integration with the owning {@link SearchController}.</li>
 * </ul>
 *
 * <p>Results are wrapped in a {@link SearchResult}, which provides a uniform
 * structure for pagination metadata consumed by the UI.
 */
public class FranchiseSearchHandler implements SearchHandler {

    /** Service used to interact with Franchise persistence operations. */
    private final FranchiseService franchiseService = AppContextHolder.getBean(FranchiseService.class);

    /** Reference to the controller that owns this handler. */
    @Setter
    private SearchController controller;

    /**
     * Returns the filter panel for franchise searches.
     * Currently no filters are implemented, so a placeholder node is returned.
     *
     * @return a placeholder {@link Node} representing the filter panel
     */
    @Override
    public Node getSearchFilters() {
        return new Label("Franchise Filters");
    }

    /**
     * Executes a paginated search for {@link Franchise} entities.
     *
     * <p>The method delegates to
     * {@link com.alejandroacg.dataverseappshowcase.services.FranchiseService#getAllFranchisesPage}
     * to retrieve a Spring Data {@link Page}, then wraps the result into a
     * {@link SearchResult} for consumption by the UI layer.
     *
     * @param page zero-based page index
     * @param size number of items per page
     * @return a {@link SearchResult} containing data and pagination metadata
     */
    @Override
    public SearchResult<Franchise> performSearch(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Franchise> resultPage = franchiseService.getAllFranchisesPage(pageable);

        return new SearchResult<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );
    }
}

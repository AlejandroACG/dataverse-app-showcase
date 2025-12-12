package com.alejandroacg.dataverseappshowcase.search;

import com.alejandroacg.dataverseappshowcase.controllers.SearchController;
import javafx.scene.Node;

/**
 * Defines the contract for all search handlers used by {@link SearchController}.
 *
 * <p>Each searchable entity type (e.g., Franchise, Genre) provides its own
 * implementation of this interface. Handlers encapsulate:
 *
 * <ul>
 *   <li>The UI filters relevant to the entity type.</li>
 *   <li>The logic required to execute paginated searches.</li>
 *   <li>An optional reference back to the owning {@link SearchController},
 *       allowing bi-directional coordination when needed.</li>
 * </ul>
 *
 * <p>This design keeps {@code SearchController} fully generic and decoupled
 * from entity-specific search behavior.
 */
public interface SearchHandler {

    /**
     * Returns the UI node containing filter controls specific to the entity type.
     *
     * <p>Examples include:
     * <ul>
     *   <li>Text fields for name-based searches</li>
     *   <li>Color/tag selectors</li>
     *   <li>Drop-downs for categorization filters</li>
     * </ul>
     *
     * @return a JavaFX Node that will be inserted into the filter panel
     */
    Node getSearchFilters();

    /**
     * Executes a paginated search operation.
     *
     * <p>SearchHandlers should:
     * <ul>
     *   <li>Retrieve the requested page of data from the database or service layer.</li>
     *   <li>Apply any filter values obtained from {@link #getSearchFilters()}.</li>
     *   <li>Return results wrapped in {@link SearchResult}, including
     *       metadata such as total pages and total element count.</li>
     * </ul>
     *
     * <p>This method is invoked asynchronously by {@link SearchController},
     * ensuring that searches never block the UI thread.
     *
     * @param page zero-based page index
     * @param size number of items per page
     * @return a populated {@link SearchResult} representing the page of results
     */
    SearchResult<?> performSearch(int page, int size);

    /**
     * Injects the owning {@link SearchController}, enabling communication
     * back to the UI layer when necessary.
     *
     * <p>Typical uses include:
     * <ul>
     *   <li>Triggering a new search when filter values change</li>
     *   <li>Accessing helper methods for debouncing or pagination</li>
     * </ul>
     *
     * @param controller the {@link SearchController} instance managing this handler
     */
    void setController(SearchController controller);
}

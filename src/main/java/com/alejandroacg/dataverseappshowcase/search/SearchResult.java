package com.alejandroacg.dataverseappshowcase.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents a standardized, immutable wrapper for paginated search results.
 *
 * <p>This DTO is used by search handlers and controllers to return a unified
 * structure for paged data, regardless of the underlying entity type or
 * where the data comes from (database, in-memory filtering, etc.).
 *
 * <p>All fields are final, making the object thread-safe and suitable for
 * being passed between background workers and the JavaFX UI thread.
 *
 * @param <T> The type of the items returned in the paginated result.
 */
@Getter
@AllArgsConstructor
public class SearchResult<T> {

    /** The list of items contained in the current page. */
    private final List<T> items;

    /**
     * Zero-based index representing the current page number.
     * Useful for updating UI pagination controls.
     */
    private final int page;

    /** The number of items per page used when retrieving this result. */
    private final int size;

    /** The total number of elements matching the query across all pages. */
    private final long totalElements;

    /** Total number of pages available for the given search parameters. */
    private final int totalPages;
}

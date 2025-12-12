package com.alejandroacg.dataverseappshowcase.search;

/**
 * Utility class used to prevent outdated asynchronous search results
 * from overwriting newer ones.
 *
 * <p>When the user types or changes filters quickly, multiple async search
 * operations may be launched. Because they complete unpredictably,
 * older results could incorrectly replace the newest results in the UI.</p>
 *
 * <p>This class assigns an incrementing request ID to each search operation.
 * The UI layer (typically {@code SearchController}) checks whether the completed
 * async operation corresponds to the latest request. If not, the result is safely ignored.</p>
 *
 * <p>Thread-safe: both methods are synchronized to ensure correctness even when
 * invoked from multiple background threads.</p>
 */
public class SearchBouncer {

    /** Tracks the most recently issued request ID. */
    private long lastRequestId = 0;

    /**
     * Generates and returns the next unique request ID.
     *
     * <p>Called each time a new async search operation begins.</p>
     *
     * @return monotonically increasing request identifier
     */
    public synchronized long nextRequestId() {
        return ++lastRequestId;
    }

    /**
     * Determines whether the given request ID corresponds to
     * the most recently issued search request.
     *
     * <p>If false, the async result should be discarded to avoid overwriting
     * newer search results.</p>
     *
     * @param requestId the ID associated with a completed async search
     * @return true if this ID matches the most recent request
     */
    public synchronized boolean isLatest(long requestId) {
        return requestId == lastRequestId;
    }
}

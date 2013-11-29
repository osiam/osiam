package org.osiam.storage.dao;

import org.osiam.storage.entities.InternalIdSkeleton;

import java.util.List;

public class SearchResult<T extends InternalIdSkeleton> {

    public final List<T> results; // NOSONAR - field is final and can be public
    public final long totalResults; // NOSONAR - field is final and can be public

    public SearchResult(List<T> results, long totalResults) {
        this.results = results;
        this.totalResults = totalResults;
    }
}

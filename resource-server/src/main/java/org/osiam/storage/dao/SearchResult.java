package org.osiam.storage.dao;

import java.util.List;

import org.osiam.storage.entities.ResourceEntity;

public class SearchResult<T extends ResourceEntity> {

    public final List<T> results; // NOSONAR - field is final and can be public
    public final long totalResults; // NOSONAR - field is final and can be public

    public SearchResult(List<T> results, long totalResults) {
        this.results = results;
        this.totalResults = totalResults;
    }
}

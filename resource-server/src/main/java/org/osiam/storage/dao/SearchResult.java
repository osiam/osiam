package org.osiam.storage.dao;

import org.osiam.storage.entities.InternalIdSkeleton;

import java.util.List;

public class SearchResult<T extends InternalIdSkeleton> {

    private final List<T> results;
    private final long totalResults;

    public SearchResult(List<T> results, long totalResults) {
        this.results = results;
        this.totalResults = totalResults;
    }

    public List<T> getResults(){
        return results;
    }

    public long getTotalResults(){
        return totalResults;
    }
}

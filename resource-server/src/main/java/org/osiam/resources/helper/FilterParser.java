package org.osiam.resources.helper;

import org.osiam.storage.entities.InternalIdSkeleton;

public interface FilterParser<T extends InternalIdSkeleton> {

    public abstract FilterChain<T> parse(String filter);

}
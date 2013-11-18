package org.osiam.resources.converter;

import org.osiam.resources.scim.Resource;
import org.osiam.storage.entities.InternalIdSkeleton;

public interface ResourceConverter<S extends Resource, E extends InternalIdSkeleton> extends Converter<S, E> {

    E fromScim(S scim);

    S toScim(E entity);
}

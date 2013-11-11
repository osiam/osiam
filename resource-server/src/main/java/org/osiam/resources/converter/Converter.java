package org.osiam.resources.converter;

import org.osiam.resources.scim.Resource;

public interface Converter<S, E> {

    E fromScim(S scim);
    
    S toScim(E entity);
}

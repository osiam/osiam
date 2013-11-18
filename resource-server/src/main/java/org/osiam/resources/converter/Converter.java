package org.osiam.resources.converter;

public interface Converter<S, E> {

    E fromScim(S scim);

    S toScim(E entity);
}

package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;

/**
 * Interface responsible of the {@Link User} serialization.
 */
public interface UserSerializationService {

    /**
     * Deserialize a given URL Encoded string into the corresponding user instance.
     * @param urlEncodedBody the URL Encoded string.
     * @return the corresponding user instance.
     * @throws UnsupportedUserSerializationException if the syntax of the URL Encoded string is wrong.
     */
    User deserialize(String urlEncodedBody) throws UnsupportedUserSerializationException;

}

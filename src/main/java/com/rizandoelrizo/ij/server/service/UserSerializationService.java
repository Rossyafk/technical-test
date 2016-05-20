package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;

public interface UserSerializationService {

    User deserialize(String requestBody) throws UnsupportedUserSerializationException;

    String serialize(User user);

}

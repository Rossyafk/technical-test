package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.User;

public interface UserSerializationService {

    User deserialize(String requestBody);

}

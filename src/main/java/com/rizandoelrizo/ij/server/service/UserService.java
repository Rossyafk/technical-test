package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findByName(String username);

    User save(User user);

}

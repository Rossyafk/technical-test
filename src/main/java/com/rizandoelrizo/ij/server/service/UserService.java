package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findByName(String username);

    User save(User user);

    User findById(Long userId) throws UserNotFoundException;

    User replaceById(Long userId, User user) throws UserNotFoundException;

    User deleteById(Long userId) throws UserNotFoundException;

}

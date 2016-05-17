package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByName(String username) {
        return userRepository.findByName(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

}

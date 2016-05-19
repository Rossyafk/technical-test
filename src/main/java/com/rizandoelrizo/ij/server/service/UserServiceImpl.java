package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.UserRepository;
import com.rizandoelrizo.ij.server.service.exception.UserNotFoundException;

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

    @Override
    public User findById(Long userId) throws UserNotFoundException {
        Optional<User> foundUser = userRepository.findById(userId);
        return foundUser.orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User replaceById(Long userId, User user) throws UserNotFoundException {
        Optional<User> replacedUser = userRepository.replaceById(userId, user);
        return replacedUser.orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User deleteById(Long userId) throws UserNotFoundException {
        Optional<User> deletedUser = userRepository.deleteById(userId);
        return deletedUser.orElseThrow(UserNotFoundException::new);
    }

}

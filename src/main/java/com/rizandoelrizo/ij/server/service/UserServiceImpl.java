package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.UserRepository;
import com.rizandoelrizo.ij.server.service.exception.UserAlreadyExistsException;
import com.rizandoelrizo.ij.server.service.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Default implementation for the UserService.
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ReentrantReadWriteLock lock =  new ReentrantReadWriteLock();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds all users
     * @return list of all users.
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Checks for duplicated name before saving the given user.
     * @param user the user to save.
     * @return instance of the saved user.
     * @throws UserAlreadyExistsException if an user with the same name already exists.
     */
    @Override
    public User checkDuplicatedAndSave(User user) throws UserAlreadyExistsException {
        Optional.ofNullable(user).orElseThrow(IllegalArgumentException::new);
        lock.writeLock().lock();

        if (userRepository.findByName(user.getName()).isPresent()) {
            lock.writeLock().unlock();
            throw new UserAlreadyExistsException(user);
        }
        User savedUser = userRepository.save(user);

        lock.writeLock().unlock();
        return savedUser;
    }

    /**
     * Returns a user with the given id.
     * @param userId id of the user.
     * @return instance of the user with the given id.
     * @throws UserNotFoundException if no user is found.
     */
    @Override
    public User findById(Long userId) throws UserNotFoundException {
        Optional<User> foundUser = userRepository.findById(userId);
        return foundUser.orElseThrow(UserNotFoundException::new);
    }

    /**
     * Replaces a user with the given id.
     * @param userId id of the user.
     * @param user the replacement user.
     * @return instance of the replaced user.
     * @throws UserNotFoundException if no user is found.
     */
    @Override
    public User replaceById(Long userId, User user) throws UserNotFoundException {
        Optional<User> replacedUser = userRepository.replaceById(userId, user);
        return replacedUser.orElseThrow(UserNotFoundException::new);
    }

    /**
     * Deletes a user with the given id.
     * @param userId id of the user.
     * @return instance of the deleted user.
     * @throws UserNotFoundException if no user is found.
     */
    @Override
    public User deleteById(Long userId) throws UserNotFoundException {
        Optional<User> deletedUser = userRepository.deleteById(userId);
        return deletedUser.orElseThrow(UserNotFoundException::new);
    }

}

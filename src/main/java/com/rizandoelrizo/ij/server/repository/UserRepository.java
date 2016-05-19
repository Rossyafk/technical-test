package com.rizandoelrizo.ij.server.repository;

import com.rizandoelrizo.ij.server.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface for generic CRUD operations on a {@link User} repository.
 */
public interface UserRepository {

    /**
     * Saves a given user.
     * @param userToSave {@link User} to save.
     * @return the saved user.
     */
    User save(User userToSave);

    /**
     * Returns all users.
     * @return all users.
     */
    List<User> findAll();

    /**
     * Returns a user with the specified name.
     * @param username name of the user.
     * @return Optional instance of the specified user.
     */
    Optional<User> findByName(String username);

    /**
     * Returns a user with the specified id.
     * @param userId id of the user.
     * @return Optional instance of the specified user.
     */
    Optional<User> findById(Long userId);

    Optional<User> replaceById(Long userId, User user);

    Optional<User> deleteById(Long userId);

}

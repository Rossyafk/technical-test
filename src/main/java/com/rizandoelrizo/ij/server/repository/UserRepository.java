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
     * @param userToSave {@link User} to checkDuplicatedAndSave.
     * @return the saved user.
     */
    User save(User userToSave);

    /**
     * Returns all users.
     * @return all users.
     */
    List<User> findAll();

    /**
     * Returns a user with the given name.
     * @param username name of the user.
     * @return Optional instance of the given user.
     */
    Optional<User> findByName(String username);

    /**
     * Returns a user with the given id.
     * @param userId id of the user.
     * @return Optional instance of the given user.
     */
    Optional<User> findById(Long userId);

    /**
     * Replaces a user with the given id.
     * @param userId id of the user.
     * @param user replacement user.
     * @return Optional instance of the replaced user.
     */
    Optional<User> replaceById(Long userId, User user);

    /**
     * Deletes a user with the given id.
     * @param userId id of the user
     * @return Optional instance of the deleted user.
     */
    Optional<User> deleteById(Long userId);

}

package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.exception.UserAlreadyExistsException;
import com.rizandoelrizo.ij.server.service.exception.UserNotFoundException;

import java.util.List;

/**
 * Interface responsible of the business management of the {@link User} entity.
 */
public interface UserService {

    /**
     * Finds all users
     * @return list of all users.
     */
    List<User> findAll();

    /**
     * Checks for duplicated name before saving the given user.
     * @param user the user to save.
     * @return instance of the saved user.
     * @throws UserAlreadyExistsException if an user with the same name already exists.
     */
    User checkDuplicatedAndSave(User user) throws UserAlreadyExistsException;

    /**
     * Returns a user with the given id.
     * @param userId id of the user.
     * @return instance of the user with the given id.
     * @throws UserNotFoundException if no user is found.
     */
    User findById(Long userId) throws UserNotFoundException;

    /**
     * Replaces a user with the given id.
     * @param userId id of the user.
     * @param user the replacement user.
     * @return instance of the replaced user.
     * @throws UserNotFoundException if no user is found.
     */
    User replaceById(Long userId, User user) throws UserNotFoundException;

    /**
     * Deletes a user with the given id.
     * @param userId id of the user.
     * @return instance of the deleted user.
     * @throws UserNotFoundException if no user is found.
     */
    User deleteById(Long userId) throws UserNotFoundException;

}

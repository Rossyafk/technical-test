package com.rizandoelrizo.ij.server.repository;

import com.rizandoelrizo.ij.server.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 *  In memory {@link User} Repository.
 */
public class InMemoryUserRepository implements UserRepository {

    private final AtomicLong sequenceUserId;

    private final Map<Long, User> users = new HashMap<>();

    private final ReentrantReadWriteLock lock =  new ReentrantReadWriteLock();

    public InMemoryUserRepository(Optional<Long> sequenceInitialValue) {
        Long initialValue = sequenceInitialValue
                .filter(value -> value >= 1L)
                .orElse(1L);
        this.sequenceUserId = new AtomicLong(initialValue);
    }

    /**
     * Saves a given user
     * @param userToSave {@link User} to save.
     * @return a saved user with an unique id.
     */
    @Override
    public User save(User userToSave) {
        lock.writeLock().lock();

        long nextId = sequenceUserId.getAndIncrement();
        User savedUser = User.of(nextId, userToSave);
        users.put(nextId, savedUser);

        lock.writeLock().unlock();
        return savedUser;
    }

    /**
     * Returns all users.
     * @return a list of all user.
     */
    @Override
    public List<User> findAll() {
        lock.readLock().lock();

        List<User> savedUsers = users.values().stream()
                .collect(Collectors.toList());

        lock.readLock().unlock();
        return savedUsers;
    }

    /**
     * Returns a user with the specified name.
     * @param username name of the user.
     * @return Optional instance of the specified user.
     */
    @Override
    public Optional<User> findByName(String username) {
        lock.readLock().lock();

        Optional<User> optionalSearchedUser = users.values().stream()
                .filter(user -> user.getName().equals(username))
                .findFirst();

        lock.readLock().unlock();
        return optionalSearchedUser;
    }

}
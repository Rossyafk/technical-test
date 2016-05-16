package com.rizandoelrizo.ij.server.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a user in the app.
 * This class is Immutable.
 */
public class User {

    private final long id;

    private final String name;

    private final String password;

    private final Set<Role> roles = new HashSet<>();

    private User(String name, String password, Optional<Set<Role>> roles) {
        this(0L, name, password, roles);
    }

    private User(long id, String name, String password, Optional<Set<Role>> roles) {
        this.id = id;
        this.name = name;
        this.password = password;
        roles.ifPresent(optionalRoles -> this.roles.addAll(optionalRoles));
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public static User of(String name, String password, Optional<Set<Role>> roles) {
        return new User(name, password, roles);
    }

    public static User of(long id, User other) {
        return new User(id, other.getName(), other.getPassword(), Optional.of(other.getRoles()));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }

    /**
     * Checks if the user contains a specif role.
     * @param expectedRole the role to check.
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(Role expectedRole) {
        return roles.stream()
                .anyMatch(role -> role.equals(expectedRole));
    }

}

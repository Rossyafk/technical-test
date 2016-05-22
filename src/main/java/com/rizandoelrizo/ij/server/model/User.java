package com.rizandoelrizo.ij.server.model;

import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Represents a user in the app.
 */
public class User {

    /**
     * Attributes of the User class, useful for the serialization process.
     */
    public enum Attribute {
        NAME("name"),
        PASSWORD("password"),
        ROLES("roles");

        private final String name;

        Attribute(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Id of the user. Incremented by the system.
     */
    private final long id;

    private final String name;

    /**
     * Password of the user. Base64 encoded, just a reminder to not to store the password in clear.
     * (Another approach should be used).
     */
    private final String password;

    private final Set<Role> roles = new HashSet<>();

    private User(String name, String password, Set<Role> roles) {
        this(0L, name, password, roles);
    }

    private User(long id, String name, String password, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roles.addAll(roles);
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

    public static User of(String name, String password, Set<Role> roles) {
        String validatedName = validateAndProcessStringValue(name);
        String validatedPassword = validateAndProcessStringValue(password);
        Optional.ofNullable(roles).orElseThrow(IllegalArgumentException::new);
        String encodedPassword = Base64.getEncoder().encodeToString(validatedPassword.getBytes(UTF_8));
        return new User(validatedName, encodedPassword, roles);
    }

    public static User of(long id, User other) {
        return new User(id, other.getName(), other.getPassword(), other.getRoles());
    }

    /**
     * Checks if the user contains a specif role.
     * @param expectedRole the role to check.
     * @return true if the user has the role, false otherwise.
     */
    public boolean hasRole(Role expectedRole) {
        return roles.stream()
                .anyMatch(role -> role.equals(expectedRole));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return roles != null ? roles.equals(user.roles) : user.roles == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }

    private static String validateAndProcessStringValue(String valueToCheck) {
        return Optional.ofNullable(valueToCheck)
                .map(String::trim)
                .filter(nameToFilter -> !nameToFilter.isEmpty())
                .orElseThrow(IllegalArgumentException::new);
    }

}

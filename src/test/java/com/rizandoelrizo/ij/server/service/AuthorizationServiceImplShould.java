package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.InMemoryUserRepository;
import com.rizandoelrizo.ij.server.repository.UserRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationServiceImplShould {

    private static AuthorizationService sut;

    private static UserRepository mockedUserRepository;

    private static String NAME = "Test";

    private static String PASSWORD = "test";

    private static User USER;

    @BeforeClass
    public static void beforeClass() {
        USER = User.of(NAME, PASSWORD, Stream.of(Role.ADMIN).collect(toSet()));
        mockedUserRepository = mock(InMemoryUserRepository.class);
        sut = new AuthorizationServiceImpl(mockedUserRepository);
    }

    @Test
    public void validate_with_correct_credentials() throws Exception {
        when(mockedUserRepository.findByName(NAME)).thenReturn(Optional.of(USER));
        assertThat(sut.isValidCredential(NAME, PASSWORD), is(true));
    }

    @Test
    public void not_validate_with_incorrect_password() throws Exception {
        when(mockedUserRepository.findByName(NAME)).thenReturn(Optional.of(USER));
        assertThat(sut.isValidCredential(NAME, "incorrect"), is(false));
    }

    @Test
    public void not_validate_with_incorrect_name() throws Exception {
        when(mockedUserRepository.findByName("incorrect")).thenReturn(Optional.empty());
        assertThat(sut.isValidCredential("incorrect", PASSWORD), is(false));
    }

    @Test
    public void validate_with_correct_user() throws Exception {
        when(mockedUserRepository.findByName(USER.getName())).thenReturn(Optional.of(USER));
        assertThat(sut.isValidCredential(USER), is(true));
    }

    @Test
    public void not_validate_with_user_with_incorrect_password() throws Exception {
        when(mockedUserRepository.findByName(USER.getName())).thenReturn(Optional.of(USER));
        assertThat(sut.isValidCredential(User.of(USER.getName(), "incorrect", emptySet())),
                is(false));
    }

    @Test
    public void not_validate_with_user_with_incorrect_name() throws Exception {
        when(mockedUserRepository.findByName(USER.getName())).thenReturn(Optional.empty());
        assertThat(sut.isValidCredential(USER), is(false));
    }

    @Test
    public void find_role_is_user() throws Exception {
        when(mockedUserRepository.findByName(USER.getName())).thenReturn(Optional.of(USER));
        assertThat(sut.isUserInRole(NAME, Role.ADMIN), is(true));
    }

    @Test
    public void not_find_role_is_user() throws Exception {
        when(mockedUserRepository.findByName(USER.getName())).thenReturn(Optional.of(USER));
        assertThat(sut.isUserInRole(NAME, Role.PAGE_1), is(false));
    }

    @Test
    public void not_find_role_if_user_not_found() throws Exception {
        when(mockedUserRepository.findByName(USER.getName())).thenReturn(Optional.empty());
        assertThat(sut.isUserInRole(NAME, Role.ADMIN), is(false));
    }

}
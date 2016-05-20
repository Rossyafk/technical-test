package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static com.rizandoelrizo.ij.server.model.Role.ADMIN;
import static com.rizandoelrizo.ij.server.model.Role.PAGE_1;
import static com.rizandoelrizo.ij.server.model.Role.PAGE_2;
import static com.rizandoelrizo.ij.server.model.Role.PAGE_3;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class UserSerializationServiceImplShould {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private UserSerializationService sut = new UserSerializationServiceImpl();

    @Test
    public void deserialize_a_user_with_admin_role() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        assertThat( parsedUserFrom("name=Test&password=test&roles=admin"),
                equalTo(getUserFrom("Test", "test", Stream.of(ADMIN).collect(toSet()))));
    }

    @Test
    public void deserialize_a_user_with_page_1_role() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        assertThat( parsedUserFrom("name=Test&password=test&roles=page_1"),
                equalTo(getUserFrom("Test", "test", Stream.of(PAGE_1).collect(toSet()))));
    }

    @Test
    public void deserialize_a_user_with_page_2_role() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        assertThat( parsedUserFrom("name=Test&password=test&roles=page_2"),
                equalTo(getUserFrom("Test", "test", Stream.of(PAGE_2).collect(toSet()))));
    }

    @Test
    public void deserialize_a_user_with_page_3_role() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        assertThat( parsedUserFrom("name=Test&password=test&roles=page_3"),
                equalTo(getUserFrom("Test", "test", Stream.of(PAGE_3).collect(toSet()))));
    }

    @Test
    public void deserialize_a_user_with_multiple_roles() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        assertThat( parsedUserFrom("name=Test&password=test&roles=admin&roles=page_1&roles=page_2&roles=page_3"),
                equalTo(getUserFrom("Test", "test", Stream.of(ADMIN, PAGE_1, PAGE_2, PAGE_3).collect(toSet()))));
    }

    @Test
    public void deserialize_a_user_without_roles() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        assertThat( parsedUserFrom("name=Test&password=test"),
                equalTo(getUserFrom("Test", "test", Collections.emptySet())));
    }

    @Test
    public void throw_exception_when_name_is_missing() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        parsedUserFrom("password=test");
    }

    @Test
    public void throw_exception_when_password_is_missing() throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        parsedUserFrom("name=Test");
    }

    @Test
    public void throw_exception_when_null() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize(null);
    }

    @Test
    public void throw_exception_when_empty() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize("");
    }

    @Test
    public void throw_exception_when_empty_name() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize("name=&password=test");
    }

    @Test
    public void throw_exception_when_empty_password() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize("name=Test&password=");
    }

    @Test
    public void throw_exception_when_role_not_valid() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize("name=Test&password=test&roles=admin&roles=administrator");
    }

    @Test
    public void deserialize_when_empty_role_is_mixed_with_valid_roles() throws UnsupportedUserSerializationException,
            UnsupportedEncodingException {
        assertThat( parsedUserFrom("name=Test&password=test&roles=admin&roles=page_1&roles="),
                equalTo(getUserFrom("Test", "test", Stream.of(ADMIN, PAGE_1).collect(toSet()))));
    }

    @Test
    public void throw_exception_when_name_is_a_space() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize("name= &password=test");
    }

    @Test
    public void throw_exception_when_password_is_a_space() throws UnsupportedUserSerializationException {
        thrown.expect(UnsupportedUserSerializationException.class);
        sut.deserialize("name=Test&password= ");
    }


    @Test
    public void serialize() throws Exception {

    }

    private User parsedUserFrom(String decodedUser) throws UnsupportedEncodingException,
            UnsupportedUserSerializationException {
        return sut.deserialize(URLEncoder.encode(decodedUser, UTF_8.name()));
    }

    private User getUserFrom(String name, String password, Set<Role> roles) {
        return User.of(name, password, roles);
    }

}
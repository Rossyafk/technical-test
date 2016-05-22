package com.rizandoelrizo.ij.server.web.handler.rest;


import com.rizandoelrizo.ij.server.HttpServerApp;
import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.model.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.stream.Stream;

import static com.rizandoelrizo.ij.server.common.HttpHeader.AUTHORIZATION;
import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpHeader.LOCATION;
import static com.rizandoelrizo.ij.server.common.HttpHeader.WWW_AUTHENTICATE;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_PLAIN_UTF8;
import static com.rizandoelrizo.ij.server.model.Role.PAGE_1;
import static com.rizandoelrizo.ij.server.model.Role.PAGE_2;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for requests to resource "/api/users"
 */
public class UsersHandlerITShould {

    private static final String URL_LIST_USERS = "http://localhost:7000/api/users";

    private static String BODY_PARAMS = "name=User9999&password=user9999&roles=page_1&roles=page_2";

    private static User POSTED_USER =
            User.of(6, User.of("User9999", "user9999", Stream.of(PAGE_1, PAGE_2).collect(toSet())));

    private static HttpServerApp SERVER_INSTANCE;

    @BeforeClass
    public static void beforeClass() throws Exception {
        BODY_PARAMS = URLEncoder.encode(BODY_PARAMS, UTF_8.name());
        SERVER_INSTANCE = new HttpServerApp(7000);
        SERVER_INSTANCE.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        SERVER_INSTANCE.stop();
    }

    @Test
    public void not_be_able_to_authenticate_without_credentials() throws IOException {
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), GET);

        assertThat(connection.getResponseCode(), is(HTTP_UNAUTHORIZED));
        assertThat(connection.getHeaderField(WWW_AUTHENTICATE.getName()), is("Basic realm=\"REST\""));
        assertThat(connection.getContentLength(), is(0));
        connection.disconnect();
    }

    @Test
    public void not_be_able_to_authenticate_with_invalid_credentials() throws IOException {
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), GET);
        String encodedUser = Base64.getEncoder().encodeToString("InvalidUser:InvalidPassword".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_UNAUTHORIZED));
        assertThat(connection.getHeaderField(WWW_AUTHENTICATE.getName()), is("Basic realm=\"REST\""));
        assertThat(connection.getContentLength(), is(0));
    }

    @Test
    public void authenticate_with_valid_credentials() throws IOException {
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), GET);
        String encodedUser = Base64.getEncoder().encodeToString("User1:user1".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_OK));
        assertThat(connection.getHeaderField(CONTENT_TYPE.getName()), is(TEXT_PLAIN_UTF8.getName()));
        assertThat(connection.getContentLength(), not(0));
    }

    @Test
    public void list_current_available_users() throws IOException {
        restartServerInstance();
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), GET);
        String encodedUser = Base64.getEncoder().encodeToString("User1:user1".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_OK));
        assertThat(connection.getHeaderField(CONTENT_TYPE.getName()), is(TEXT_PLAIN_UTF8.getName()));

        String expectedBody = HttpServerApp.INITIAL_USERS.toString();
        assertThat(connection.getContentLength(), is(expectedBody.length()));
        assertThat(expectedBody, is(readBody(connection)));
        connection.disconnect();
    }

    @Test
    public void not_be_able_to_post_a_new_user_without_proper_role() throws IOException {
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), POST);
        String encodedUser = Base64.getEncoder().encodeToString("User1:user1".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_FORBIDDEN));
        assertThat(connection.getContentLength(), is(0));
    }

    @Test
    public void post_a_new_user() throws IOException {
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), POST);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_CREATED));
        assertThat(connection.getHeaderField(CONTENT_TYPE.getName()), is(TEXT_PLAIN_UTF8.getName()));
        assertThat(connection.getContentLength(), is(POSTED_USER.toString().getBytes(UTF_8).length));
        assertThat(connection.getHeaderField(LOCATION.getName()), is(URL_LIST_USERS + "/" + POSTED_USER.getId()));
        assertThat(POSTED_USER.toString(), is(readBody(connection)));
    }

    @Test
    public void not_be_able_to_post_the_same_user_more_that_one_time() throws IOException {
        restartServerInstance();
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), POST);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_CREATED));
        assertThat(connection.getHeaderField(CONTENT_TYPE.getName()), is(TEXT_PLAIN_UTF8.getName()));
        assertThat(connection.getContentLength(), is(POSTED_USER.toString().getBytes(UTF_8).length));
        assertThat(connection.getHeaderField(LOCATION.getName()), is(URL_LIST_USERS + "/" + POSTED_USER.getId()));
        assertThat(POSTED_USER.toString(), is(readBody(connection)));

        connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), POST);
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_CONFLICT));
        assertThat(connection.getContentLength(), is(0));
    }

    @Test
    public void not_be_able_to_post_with_bad_params() throws IOException {
        restartServerInstance();
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), POST);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write("invented=test&password=Test".getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_BAD_REQUEST));
        assertThat(connection.getContentLength(), is(0));
    }

    @Test
    public void not_be_able_to_post_without_proper_content_type() throws IOException {
        restartServerInstance();
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), POST);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), "");

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_UNSUPPORTED_TYPE));
        assertThat(connection.getContentLength(), is(0));
    }

    private HttpURLConnection getHttpURLConnectionFor(URL url, HttpMethod httpMethod) throws IOException {
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setRequestMethod(httpMethod.name());
        return client;
    }

    private String readBody(HttpURLConnection connection) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8))) {
            return buffer.lines().collect(joining());
        }
    }

    private void restartServerInstance() throws IOException {
        SERVER_INSTANCE.stop();
        SERVER_INSTANCE = new HttpServerApp(7000);
        SERVER_INSTANCE.start();
    }

}

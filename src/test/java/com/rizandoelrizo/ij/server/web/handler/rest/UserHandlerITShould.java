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

import static com.rizandoelrizo.ij.server.common.HttpHeader.AUTHORIZATION;
import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpHeader.LOCATION;
import static com.rizandoelrizo.ij.server.common.HttpMethod.DELETE;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.PUT;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_PLAIN_UTF8;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for requests to resource "/api/users/{id}"
 */
public class UserHandlerITShould {

    private static final String URL_USER = "http://localhost:7000/api/users/%d";

    private static String BODY_PARAMS = "name=User9999&password=user9999&roles=page_3";

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
    public void get_admin_user() throws IOException {
        User expectedUser = HttpServerApp.INITIAL_USERS.stream()
                .filter(user -> user.getName().equals("Admin"))
                .findFirst().get();

        HttpURLConnection connection =
                getHttpURLConnectionFor(new URL(String.format(URL_USER, expectedUser.getId())), GET);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_OK));
        assertThat(connection.getHeaderField(CONTENT_TYPE.getName()), is(TEXT_PLAIN_UTF8.getName()));

        String expectedBody = expectedUser.toString();
        assertThat(connection.getContentLength(), is(expectedBody.length()));
        assertThat(expectedBody, is(readBody(connection)));
        connection.disconnect();
    }

    @Test
    public void not_get_user() throws IOException {
        HttpURLConnection connection =
                getHttpURLConnectionFor(new URL(String.format(URL_USER, 0)), GET);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_NOT_FOUND));
        assertThat(connection.getContentLength(), is(0));
        connection.disconnect();
    }

    @Test
    public void delete_user123() throws IOException {
        User expectedUser = HttpServerApp.INITIAL_USERS.stream()
                .filter(user -> user.getName().equals("User123"))
                .findFirst().get();

        HttpURLConnection connection =
                getHttpURLConnectionFor(new URL(String.format(URL_USER, expectedUser.getId())), DELETE);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_OK));
        assertThat(connection.getContentLength(), is(0));
        connection.disconnect();

        connection = getHttpURLConnectionFor(new URL(String.format(URL_USER, expectedUser.getId())), GET);
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_NOT_FOUND));
        assertThat(connection.getContentLength(), is(0));
    }

    @Test
    public void not_delete_user() throws IOException {
        HttpURLConnection connection =
                getHttpURLConnectionFor(new URL(String.format(URL_USER, 0)), DELETE);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);

        assertThat(connection.getResponseCode(), is(HTTP_NOT_FOUND));
        assertThat(connection.getContentLength(), is(0));
        connection.disconnect();
    }

    @Test
    public void replace_user3() throws IOException {
        User expectedUser = HttpServerApp.INITIAL_USERS.stream()
                .filter(user -> user.getName().equals("User3"))
                .findFirst().get();

        HttpURLConnection connection =
                getHttpURLConnectionFor(new URL(String.format(URL_USER, expectedUser.getId())), PUT);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_CREATED));
        assertThat(connection.getHeaderField(CONTENT_TYPE.getName()), is(TEXT_PLAIN_UTF8.getName()));
        assertThat(connection.getContentLength(), is(expectedUser.toString().getBytes(UTF_8).length));
        assertThat(connection.getHeaderField(LOCATION.getName()), is(String.format(URL_USER, expectedUser.getId())));
    }

    @Test
    public void not_replace_user() throws IOException {
        HttpURLConnection connection =
                getHttpURLConnectionFor(new URL(String.format(URL_USER, 0)), PUT);
        String encodedUser = Base64.getEncoder().encodeToString("Admin:admin".getBytes(UTF_8));
        connection.setRequestProperty(AUTHORIZATION.getName(), "Basic " + encodedUser);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE.getName(), FORM_URL_ENCODED.getName());

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(BODY_PARAMS.getBytes(UTF_8));
        outputStream.close();

        assertThat(connection.getResponseCode(), is(HTTP_NOT_FOUND));
        assertThat(connection.getContentLength(), is(0));
        connection.disconnect();
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

}

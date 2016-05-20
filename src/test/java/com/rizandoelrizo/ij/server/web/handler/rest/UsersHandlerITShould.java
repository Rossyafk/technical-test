package com.rizandoelrizo.ij.server.web.handler.rest;


import com.rizandoelrizo.ij.server.HttpServerApp;
import com.rizandoelrizo.ij.server.common.HttpMethod;
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
import java.util.stream.Collectors;

import static com.rizandoelrizo.ij.server.common.HttpHeader.AUTHORIZATION;
import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpHeader.WWW_AUTHENTICATE;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_PLAIN_UTF8;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for requests to resource "/api/users"
 */
public class UsersHandlerITShould {

    private static final String URL_LIST_USERS = "http://localhost:8000/users";

    private static String BODY_PARAMS = "name=User9999&password=user9999&roles=page_1&roles=page_2";

    @BeforeClass
    public static void beforeClass() throws Exception {
        BODY_PARAMS = URLEncoder.encode(BODY_PARAMS, UTF_8.name());
        HttpServerApp.main(null);
    }

    @Test
    public void not_be_able_to_authenticate_without_credentials() throws IOException {
        HttpURLConnection connection = getHttpURLConnectionFor(new URL(URL_LIST_USERS), GET);

        assertThat(connection.getResponseCode(), is(HTTP_UNAUTHORIZED));
        assertThat(connection.getHeaderField(WWW_AUTHENTICATE.getName()), is("Basic realm=\"REST\""));
        assertThat(connection.getContentLength(), is(0));
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

//    @Test
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
        assertThat(connection.getContentLength(), is(0));
    }

    private HttpURLConnection getHttpURLConnectionFor(URL url, HttpMethod httpMethod) throws IOException {
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setRequestMethod(httpMethod.name());
        return client;
    }

    private String readBody(HttpURLConnection connection) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8))) {
            return buffer.lines().collect(Collectors.joining());
        }
    }

}

package com.rizandoelrizo.ij.server.web.handler.rest;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.UserSerializationService;
import com.rizandoelrizo.ij.server.service.UserService;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;
import com.rizandoelrizo.ij.server.service.exception.UserAlreadyExistsException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpHeader.LOCATION;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_PLAIN_UTF8;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Handler for REST requests to "/api/users"
 */
public class UsersHandler extends RestHandler {

    private final HttpMethod[] allowedHttpMethods = new HttpMethod[]{GET, POST};

    private final UserService userService;

    private final AuthorizationService authorizationService;

    private final UserSerializationService userSerializationService;

    public UsersHandler(UserService userService, AuthorizationService authorizationService,
                        UserSerializationService userSerializationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
        this.userSerializationService = userSerializationService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            service(exchange);
        } catch (UnsupportedUserSerializationException e) {
            returnBadRequest(exchange);
        } catch (UserAlreadyExistsException e) {
            returnConflict(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            returnInternalServerError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void service(HttpExchange exchange) throws IOException, UnsupportedUserSerializationException,
            UserAlreadyExistsException {
        switch (HttpMethod.valueOf(exchange.getRequestMethod())) {
            case GET:
                doGet(exchange);
                break;
            case POST:
                doProtectedPost(exchange, Role.ADMIN);
                break;
            default:
                returnMethodNotAllowed(exchange, allowedHttpMethods);
        }
    }

    private void doGet(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());

        List<User> users = userService.findAll();
        byte[] response = users.toString().getBytes(UTF_8);

        exchange.sendResponseHeaders(HTTP_OK, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();

        exchange.close();
    }

    private void doProtectedPost(HttpExchange exchange, Role expectedRole) throws IOException,
            UnsupportedUserSerializationException, UserAlreadyExistsException {
        HttpPrincipal principal = exchange.getPrincipal();
        if (authorizationService.isUserInRole(principal.getUsername(), expectedRole)) {
            doPost(exchange);
        } else {
            returnNotAuthorized(exchange);
        }
    }

    private void doPost(HttpExchange exchange) throws IOException, UnsupportedUserSerializationException,
            UserAlreadyExistsException {
        if (!containsContentType(exchange, FORM_URL_ENCODED)) {
            returnUnsupportedMediaType(exchange);
        } else {
            String requestBody = readRequestBody(exchange);
            User postedUser = userSerializationService.deserialize(requestBody);
            User savedUser = userService.checkDuplicatedAndSave(postedUser);
            byte[] response = savedUser.toString().getBytes(UTF_8);

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());
            responseHeaders.add(LOCATION.getName(), getAbsoluteUrlFor(exchange, savedUser.getId()));

            exchange.sendResponseHeaders(HTTP_CREATED, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();

            exchange.close();
        }
    }

}

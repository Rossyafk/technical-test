package com.rizandoelrizo.ij.server.web.handler.rest;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.UserSerializationService;
import com.rizandoelrizo.ij.server.service.UserService;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;
import com.rizandoelrizo.ij.server.service.exception.UserNotFoundException;
import com.rizandoelrizo.ij.server.web.handler.AbstractBaseHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpHeader.LOCATION;
import static com.rizandoelrizo.ij.server.common.HttpMethod.DELETE;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.PUT;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_PLAIN_UTF8;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Handler for REST requests to "/api/users/{id}"
 */
public class UserHandler extends AbstractBaseHandler {

    public static final Pattern URL_PATTERN = Pattern.compile("/api/users/(\\d{1,15}?)");

    private final HttpMethod[] allowedHttpMethods = new HttpMethod[]{GET, PUT, DELETE};

    private final UserService userService;

    private final AuthorizationService authorizationService;

    private final UserSerializationService userSerializationService;

    public UserHandler(UserService userService, AuthorizationService authorizationService,
                        UserSerializationService userSerializationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
        this.userSerializationService = userSerializationService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            service(exchange);
        } catch (UserNotFoundException e) {
            returnResourceNotFound(exchange);
        } catch (UnsupportedUserSerializationException e) {
            returnBadRequest(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            returnInternalServerError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void service(HttpExchange exchange) throws IOException, UserNotFoundException,
            UnsupportedUserSerializationException {
        switch (HttpMethod.valueOf(exchange.getRequestMethod())) {
            case GET:
                doGet(exchange);
                break;
            case PUT:
                doProtectedPut(exchange, Role.ADMIN);
                break;
            case DELETE:
                doProtectedDelete(exchange, Role.ADMIN);
                break;
            default:
                returnMethodNotAllowed(exchange, allowedHttpMethods);
        }
    }

    private void doGet(HttpExchange exchange) throws IOException, UserNotFoundException {
        User user = userService.findById(getEntityIdFrom(exchange));
        byte[] response = user.toString().getBytes(UTF_8);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());
        exchange.sendResponseHeaders(HTTP_OK, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private void doProtectedPut(HttpExchange exchange, Role expectedRole) throws IOException, UserNotFoundException,
            UnsupportedUserSerializationException {
        HttpPrincipal principal = exchange.getPrincipal();
        if (authorizationService.isUserInRole(principal.getUsername(), expectedRole)) {
            doPut(exchange);
        } else {
            returnNotAuthorized(exchange);
        }
    }

    private void doPut(HttpExchange exchange) throws IOException, UserNotFoundException,
            UnsupportedUserSerializationException {
        if (!containsContentType(exchange, FORM_URL_ENCODED)) {
            returnUnsupportedMediaType(exchange);
        } else {
            String requestBody = readRequestBody(exchange);
            User puttedUser = userSerializationService.deserialize(requestBody);
            User replacedUser = userService.replaceById(getEntityIdFrom(exchange), puttedUser);
            byte[] response = replacedUser.toString().getBytes(UTF_8);

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());
            responseHeaders.add(LOCATION.getName(), getAbsoluteUrlFor(exchange));
            exchange.sendResponseHeaders(HTTP_CREATED, response.length);

            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    private void doProtectedDelete(HttpExchange exchange, Role expectedRole) throws IOException, UserNotFoundException {
        HttpPrincipal principal = exchange.getPrincipal();
        if (authorizationService.isUserInRole(principal.getUsername(), expectedRole)) {
            doDelete(exchange);
        } else {
            returnNotAuthorized(exchange);
        }
    }

    private void doDelete(HttpExchange exchange) throws IOException, UserNotFoundException {
        userService.deleteById(getEntityIdFrom(exchange));
        exchange.sendResponseHeaders(HTTP_OK, -1);
    }

}

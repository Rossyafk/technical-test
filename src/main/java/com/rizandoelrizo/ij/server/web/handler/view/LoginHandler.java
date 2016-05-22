package com.rizandoelrizo.ij.server.web.handler.view;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.UserSerializationService;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;
import com.rizandoelrizo.ij.server.web.handler.rest.RestHandler;
import com.rizandoelrizo.ij.server.web.view.LoginView;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_HTML_UTF8;
import static java.net.HttpURLConnection.HTTP_OK;

public class LoginHandler extends RestHandler{

    private final HttpMethod[] allowedHttpMethods = new HttpMethod[]{GET, POST};

    private final UserSerializationService userSerializationService;
    private final AuthorizationService authorizationService;

    public LoginHandler(UserSerializationService userSerializationService, AuthorizationService authorizationService) {
        this.userSerializationService = userSerializationService;
        this.authorizationService = authorizationService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            service(exchange);
        } catch (IOException e) {
            e.printStackTrace();

        } catch (UnsupportedUserSerializationException e) {
            e.printStackTrace();

        } finally {
            exchange.close();
        }
    }

    private void service(HttpExchange exchange) throws IOException, UnsupportedUserSerializationException {
        switch (HttpMethod.valueOf(exchange.getRequestMethod())) {
            case GET:
                doGet(exchange);
                break;
            case POST:
                doPost(exchange);
                break;
            default:
                returnMethodNotAllowed(exchange, allowedHttpMethods);
        }
    }

    private void doGet(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(CONTENT_TYPE.getName(), TEXT_HTML_UTF8.getName());
        exchange.sendResponseHeaders(HTTP_OK, 0);

        LoginView loginView = new LoginView();
        OutputStream os = exchange.getResponseBody();
        loginView.writeTo(os);
        os.close();

        exchange.close();
    }

    private void doPost(HttpExchange exchange) throws IOException, UnsupportedUserSerializationException {
        if (!containsContentType(exchange, FORM_URL_ENCODED)) {
            returnUnsupportedMediaType(exchange);
        } else {
            String requestBody = readRequestBody(exchange);
            User postedUser = userSerializationService.deserialize(requestBody);
            if (authorizationService.isValidCredential(postedUser)) {

            } else {

            }
//            User savedUser = userService.checkDuplicatedAndSave(postedUser);
//            byte[] response = savedUser.toString().getBytes(UTF_8);
//
//            Headers responseHeaders = exchange.getResponseHeaders();
//            responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());
//            responseHeaders.add(LOCATION.getName(), "/api/users/" + savedUser.getId());
//
//            exchange.sendResponseHeaders(HTTP_CREATED, response.length);
//            OutputStream os = exchange.getResponseBody();
//            os.write(response);
//            os.close();
//
//            exchange.close();
        }

    }
}

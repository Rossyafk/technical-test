package com.rizandoelrizo.ij.server.web.handler.view;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.UserSerializationService;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;
import com.rizandoelrizo.ij.server.web.handler.AbstractBaseHandler;
import com.rizandoelrizo.ij.server.web.view.LoginSuccessView;
import com.rizandoelrizo.ij.server.web.view.LoginView;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_HTML_UTF8;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Handler to perform a login.
 */
public class LoginHandler extends AbstractBaseHandler {

    public static final Pattern URL_PATTERN = Pattern.compile("/views/public/login");

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
            returnInternalServerError(exchange);
        } catch (UnsupportedUserSerializationException e) {
            e.printStackTrace();
            returnBadRequest(exchange);
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
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add(CONTENT_TYPE.getName(), TEXT_HTML_UTF8.getName());
            exchange.sendResponseHeaders(HTTP_OK, 0);

            if (authorizationService.isValidCredential(postedUser)) {
                LoginSuccessView loginView = new LoginSuccessView();
                OutputStream os = exchange.getResponseBody();
                loginView.writeTo(os);
                os.close();
            } else {
                LoginView loginView = new LoginView();
                OutputStream os = exchange.getResponseBody();
                loginView.writeTo(os);
                os.close();
            }

            exchange.close();
        }

    }
}

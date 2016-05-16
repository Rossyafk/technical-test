package com.rizandoelrizo.ij.server;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.InMemoryUserRepository;
import com.rizandoelrizo.ij.server.repository.UserRepository;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.RoleAuthorizationService;
import com.rizandoelrizo.ij.server.service.UserService;
import com.rizandoelrizo.ij.server.service.DefaultUserService;
import com.rizandoelrizo.ij.server.web.controller.RestHandler;
import com.rizandoelrizo.ij.server.web.security.RestAuthentication;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServerApp {

	private final static Logger LOG = Logger.getLogger(HttpServerApp.class.getName());

	public static void main(String[] args) throws Exception {
		// Context
		UserRepository userRepository = new InMemoryUserRepository(Optional.of(1L));
		UserService userService = new DefaultUserService(userRepository);
		AuthorizationService authorizationService = new RoleAuthorizationService(userRepository);

		// Initialization
		userRepository.save(User.of("Pepito", "pepito", Optional.of(Collections.singleton(Role.ADMIN))));
		userRepository.save(User.of("Paquita", "paquita", Optional.empty()));


		int port = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		LOG.log(Level.INFO, "Listening on address: {0}:{1}",
				new Object[]{server.getAddress().getHostName(), String.valueOf(port)});

		HttpContext restContext = server.createContext("/api", new RestHandler(userService, authorizationService));
		restContext.setAuthenticator(new RestAuthentication("REST"));

		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}

}

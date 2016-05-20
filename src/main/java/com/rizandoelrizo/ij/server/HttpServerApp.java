package com.rizandoelrizo.ij.server;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.InMemoryUserRepository;
import com.rizandoelrizo.ij.server.repository.UserRepository;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.AuthorizationServiceImpl;
import com.rizandoelrizo.ij.server.service.UserSerializationService;
import com.rizandoelrizo.ij.server.service.UserSerializationServiceImpl;
import com.rizandoelrizo.ij.server.service.UserService;
import com.rizandoelrizo.ij.server.service.UserServiceImpl;
import com.rizandoelrizo.ij.server.web.handler.rest.UserHandler;
import com.rizandoelrizo.ij.server.web.handler.rest.UsersHandler;
import com.rizandoelrizo.ij.server.web.security.RestAuthentication;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class HttpServerApp {

	public final static List<User> INITIAL_USERS = new ArrayList<>();

	private final static Logger LOG = Logger.getLogger(HttpServerApp.class.getName());

	static {
		INITIAL_USERS.add(User.of("Admin", "admin", Stream.of(Role.ADMIN).collect(toSet())));
		INITIAL_USERS.add(User.of("User1", "user1", Stream.of(Role.PAGE_1).collect(toSet())));
		INITIAL_USERS.add(User.of("User2", "user1", Stream.of(Role.PAGE_2).collect(toSet())));
		INITIAL_USERS.add(User.of("User3", "user1", Stream.of(Role.PAGE_3).collect(toSet())));
		INITIAL_USERS.add(User.of("User123", "user123", Stream.of(Role.PAGE_1, Role.PAGE_2, Role.PAGE_3).collect(toSet())));
	}

	public static void main(String[] args) throws Exception {
		// Context
		UserRepository userRepository = new InMemoryUserRepository(1L);
		UserService userService = new UserServiceImpl(userRepository);
		AuthorizationService authorizationService = new AuthorizationServiceImpl(userRepository);
		UserSerializationService userSerializationService = new UserSerializationServiceImpl();
		UsersHandler usersHandler = new UsersHandler(userService, authorizationService, userSerializationService);
		UserHandler userHandler = new UserHandler(userService, authorizationService, userSerializationService);

		// Initialization
		INITIAL_USERS.replaceAll(userRepository::save);


		int port = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		LOG.log(Level.INFO, "Listening on address: {0}:{1}",
				new Object[]{server.getAddress().getHostName(), String.valueOf(port)});

		HttpContext usersContext = server.createContext("/users", usersHandler);
		usersContext.setAuthenticator(new RestAuthentication("REST", authorizationService));

		HttpContext userContext = server.createContext("/api/user", userHandler);
		userContext.setAuthenticator(new RestAuthentication("REST", authorizationService));

		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}

}

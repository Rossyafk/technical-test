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
import com.rizandoelrizo.ij.server.web.handler.view.LoginHandler;
import com.rizandoelrizo.ij.server.web.security.RestAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Starts and executes the Sun HttpServer.
 */
public class HttpServerApp {

	private final static Logger LOG = Logger.getLogger(HttpServerApp.class.getName());

	public final static List<User> INITIAL_USERS = new ArrayList<>();

	static {
		INITIAL_USERS.add(User.of("Admin", "admin", Stream.of(Role.ADMIN).collect(toSet())));
		INITIAL_USERS.add(User.of("User1", "user1", Stream.of(Role.PAGE_1).collect(toSet())));
		INITIAL_USERS.add(User.of("User2", "user1", Stream.of(Role.PAGE_2).collect(toSet())));
		INITIAL_USERS.add(User.of("User3", "user1", Stream.of(Role.PAGE_3).collect(toSet())));
		INITIAL_USERS.add(User.of("User123", "user123", Stream.of(Role.PAGE_1, Role.PAGE_2, Role.PAGE_3).collect(toSet())));
	}

	private final HttpServer server;

	public HttpServerApp(int port) throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(port), 0);
		LOG.log(Level.INFO, "Listening on address: {0}:{1}",
				new Object[]{server.getAddress().getHostName(), String.valueOf(port)});

		init();
	}

	public void start() {
		this.server.start();
	}

	public void stop() {
		this.server.stop(0);
	}

	private void init() throws IOException {
		// Context
		UserRepository userRepository = new InMemoryUserRepository(1L);
		UserService userService = new UserServiceImpl(userRepository);
		AuthorizationService authorizationService = new AuthorizationServiceImpl(userRepository);
		UserSerializationService userSerializationService = new UserSerializationServiceImpl();
		UsersHandler usersHandler = new UsersHandler(userService, authorizationService, userSerializationService);
		UserHandler userHandler = new UserHandler(userService, authorizationService, userSerializationService);
		LoginHandler loginHandler = new LoginHandler(userSerializationService, authorizationService);

		// Initialization
		INITIAL_USERS.replaceAll(userRepository::save);

		HttpContext usersContext = server.createContext("/users", usersHandler);
		usersContext.setAuthenticator(new RestAuthenticator("REST", authorizationService));

		HttpContext userContext = server.createContext("/api/user", userHandler);
		userContext.setAuthenticator(new RestAuthenticator("REST", authorizationService));

		HttpContext loginContext = server.createContext("/views/public/login", loginHandler);

		server.setExecutor(Executors.newCachedThreadPool());
	}

	/**
	 * Entry point of the app for external execution.
     */
	public static void main(String[] args) throws Exception {
		HttpServerApp serverApp = new HttpServerApp(8000);
		serverApp.start();
	}

}

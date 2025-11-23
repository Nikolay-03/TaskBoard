package com.example.kanban;

import com.example.kanban.config.AppConfig;
import com.example.kanban.db.ConnectionManager;
import com.example.kanban.http.Router;
import com.example.kanban.repository.UserRepository;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        AppConfig config = new AppConfig();
        ConnectionManager cm = new ConnectionManager(config);

        UserRepository userRepo = new UserRepository(cm);
        AuthService authService = new AuthService(userRepo, cm);

        int port = config.getServerPort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        Router router = new Router(server, authService, cm);
        router.registerRoutes();

        server.setExecutor(null); // default executor
        log.info("Server started on port " + port);
        server.start();
    }
}

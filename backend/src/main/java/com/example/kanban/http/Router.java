package com.example.kanban.http;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.http.handlers.AuthHandler;
import com.example.kanban.http.handlers.BoardHandler;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpServer;

public class Router {

    private final HttpServer server;
    private final AuthService authService;
    private final ConnectionManager cm;

    public Router(HttpServer server, AuthService authService, ConnectionManager cm) {
        this.server = server;
        this.authService = authService;
        this.cm = cm;
    }

    public void registerRoutes() {
        server.createContext("/api/auth", new AuthHandler(authService));
        server.createContext("/api/boards", new BoardHandler(authService, cm));
        // по аналогии: /api/tasks, /api/columns и т.п.
    }
}

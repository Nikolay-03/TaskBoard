package com.example.kanban.http;

import com.example.kanban.http.handlers.AuthHandler;
import com.example.kanban.http.handlers.BoardHandler;
import com.example.kanban.service.AuthService;
import com.example.kanban.service.BoardService;
import com.sun.net.httpserver.HttpServer;

public class Router {

    private final HttpServer server;
    private final AuthService authService;
    private final BoardService boardService;

    public Router(HttpServer server, AuthService authService, BoardService boardService) {
        this.server = server;
        this.authService = authService;
        this.boardService = boardService;
    }

    public void registerRoutes() {
        server.createContext("/api/auth", new AuthHandler(authService));
        server.createContext("/api/boards", new BoardHandler(authService, boardService));
    }
}

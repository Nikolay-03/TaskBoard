package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.User;
import com.example.kanban.repository.UserRepository;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class UserHandler implements HttpHandler {

    private final AuthService authService;
    private final UserRepository userRepository;

    public UserHandler(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        try {
            User user = requireAuth(exchange);
            if (user == null) return;
            String[] parts = path.split("/");
            if ("GET".equalsIgnoreCase(method)
                    && parts.length == 3
                    && "api".equals(parts[1])
                    && "me".equals(parts[2])
            ) {
                handleGetMe(exchange, user);
            } else if ("PATCH".equalsIgnoreCase(method)
                    && parts.length == 3
                    && "api".equals(parts[1])
                    && "me".equals(parts[2])
            ) {

                handleUpdateMe(exchange, user);
            } else {
                sendError(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleGetMe(HttpExchange ex, User user) throws IOException {
        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, user);
        }
    }

    private void handleUpdateMe(HttpExchange ex, User currentUser) throws IOException, SQLException {
        UpdateUserRequest req = JsonUtils.readJson(ex.getRequestBody(), UpdateUserRequest.class);
        if (req == null) {
            sendError(ex, 400, "Invalid request");
            return;
        }

        // Обновляем только переданные поля
        String newName = req.name != null ? req.name : currentUser.getName();
        String newAvatar = req.avatar != null ? req.avatar : currentUser.getAvatar();

        User updatedUser = userRepository.updateUser(currentUser.getId(), newName, newAvatar);

        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, updatedUser);
        }
    }

    private User requireAuth(HttpExchange ex) throws IOException {
        String cookie = ex.getRequestHeaders().getFirst("Cookie");
        String sid = null;
        if (cookie != null) {
            for (String part : cookie.split(";")) {
                part = part.trim();
                if (part.startsWith("SESSION_ID=")) {
                    sid = part.substring("SESSION_ID=".length());
                }
            }
        }
        if (sid == null || sid.isBlank()) {
            sendError(ex, 401, "No session");
            return null;
        }
        try {
            User u = authService.getUserBySession(sid);
            if (u == null) {
                sendError(ex, 401, "Invalid session");
                return null;
            }
            return u;
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, "Internal server error");
            return null;
        }
    }

    private void sendError(HttpExchange ex, int status, String msg) throws IOException {
        ex.sendResponseHeaders(status, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, java.util.Map.of("error", msg));
        }
    }

    public static class UpdateUserRequest {
        public String name;
        public String avatar;
    }
}


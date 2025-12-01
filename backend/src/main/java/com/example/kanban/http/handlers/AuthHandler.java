package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.User;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class AuthHandler implements HttpHandler {

    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        try {
            if (path.endsWith("/register") && "POST".equalsIgnoreCase(method)) {
                handleRegister(exchange);
            } else if (path.endsWith("/login") && "POST".equalsIgnoreCase(method)) {
                handleLogin(exchange);
            } else if (path.endsWith("/logout") && "POST".equalsIgnoreCase(method)) {
                handleLogout(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleRegister(HttpExchange ex) throws Exception {
        RegisterRequest req = JsonUtils.readJson(ex.getRequestBody(), RegisterRequest.class);
        if (req == null || req.email == null || req.password == null || req.name == null) {
            sendError(ex, 400, "Invalid request");
            return;
        }

        User u = authService.register(req.email, req.password, req.name, req.avatar);

        String sessionId = authService.createSession(u.getId());

        ex.getResponseHeaders().add(
                "Set-Cookie",
                "SESSION_ID=" + sessionId + "; Path=/; HttpOnly"
        );
        ex.sendResponseHeaders(201, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, u);
        }
    }

    private void handleLogin(HttpExchange ex) throws Exception {
        LoginRequest req = JsonUtils.readJson(ex.getRequestBody(), LoginRequest.class);
        if (req == null || req.email == null || req.password == null) {
            sendError(ex, 400, "Invalid request");
            return;
        }

        User u = authService.login(req.email, req.password);
        if (u == null) {
            sendError(ex, 401, "Invalid credentials");
            return;
        }

        String sessionId = authService.createSession(u.getId());

        ex.getResponseHeaders().add(
                "Set-Cookie",
                "SESSION_ID=" + sessionId + "; Path=/; HttpOnly"
        );

        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, Map.of(
                    "user", u,
                    "sessionId", sessionId
            ));
        }
    }

    private void handleLogout(HttpExchange ex) throws Exception {
        String sessionId = extractSessionId(ex);
        if (sessionId != null) {
            authService.logout(sessionId);
        }
        ex.sendResponseHeaders(204, -1);
    }


    private String extractSessionId(HttpExchange ex) {
        String cookie = ex.getRequestHeaders().getFirst("Cookie");
        if (cookie != null) {
            for (String part : cookie.split(";")) {
                String trimmed = part.trim();
                if (trimmed.startsWith("SESSION_ID=")) {
                    return trimmed.substring("SESSION_ID=".length());
                }
            }
        }
        return null;
    }

    private void sendError(HttpExchange ex, int status, String msg) throws IOException {
        ex.sendResponseHeaders(status, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, Map.of("error", msg));
        }
    }

    private void sendNotFound(HttpExchange ex) throws IOException {
        sendError(ex, 404, "Not found");
    }


    public static class RegisterRequest {
        public String email;
        public String password;
        public String name;
        public String avatar;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
}

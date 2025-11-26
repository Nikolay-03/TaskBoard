package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.repository.TaskLabelRepository;
import com.example.kanban.model.User;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class LabelHandler implements HttpHandler {

    private final AuthService authService;
    private final TaskLabelRepository labelRepository;

    public LabelHandler(AuthService authService, TaskLabelRepository labelRepository) {
        this.authService = authService;
        this.labelRepository = labelRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            User user = requireAuth(exchange);
            if (user == null) return;

            List<?> labels = labelRepository.findAll();

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                JsonUtils.writeJson(os, labels);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
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
}

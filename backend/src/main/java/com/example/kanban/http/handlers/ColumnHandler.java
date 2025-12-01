package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.Column;
import com.example.kanban.model.User;
import com.example.kanban.repository.ColumnRepository;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ColumnHandler implements HttpHandler {

    private final AuthService authService;
    private final ColumnRepository columnRepository;

    public ColumnHandler(AuthService authService, ColumnRepository columnRepository) {
        this.authService = authService;
        this.columnRepository = columnRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        try {
            String[] parts = path.split("/");

            if ("POST".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "columns".equals(parts[2])) {
                long boardId = Long.parseLong(parts[3]);
                handleCreateColumn(exchange, boardId);

            } else if ("PATCH".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "columns".equals(parts[2])) {
                long columnId = Long.parseLong(parts[3]);
                handleUpdateColumn(exchange, columnId);

            } else if ("DELETE".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "columns".equals(parts[2])) {
                long columnId = Long.parseLong(parts[3]);
                handleDeleteColumn(exchange, columnId);

            } else {
                sendError(exchange, 404, "Not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleCreateColumn(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        CreateColumnRequest req = JsonUtils.readJson(ex.getRequestBody(), CreateColumnRequest.class);
        if (req == null || req.title == null || req.title.isBlank()) {
            sendError(ex, 400, "title is required");
            return;
        }

        Integer position = req.position != null ? req.position : null;
        Column column = columnRepository.createColumn(boardId, req.title, position);
        
        ex.sendResponseHeaders(201, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, column);
        }
    }

    private void handleUpdateColumn(HttpExchange ex, long columnId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        Column existingColumn = columnRepository.findById(columnId);
        if (existingColumn == null) {
            sendError(ex, 404, "Column not found");
            return;
        }

        UpdateColumnRequest req = JsonUtils.readJson(ex.getRequestBody(), UpdateColumnRequest.class);
        if (req == null) {
            sendError(ex, 400, "Invalid request");
            return;
        }

        String title = req.title != null ? req.title : existingColumn.getTitle();
        Integer position = req.position != null ? req.position : null;

        Column updatedColumn = columnRepository.updateColumn(columnId, title, position);
        if (updatedColumn == null) {
            sendError(ex, 404, "Column not found");
            return;
        }

        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, updatedColumn);
        }
    }

    private void handleDeleteColumn(HttpExchange ex, long columnId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        Column existingColumn = columnRepository.findById(columnId);
        if (existingColumn == null) {
            sendError(ex, 404, "Column not found");
            return;
        }

        columnRepository.deleteColumn(columnId);
        ex.sendResponseHeaders(204, -1);
    }

    private User requireAuth(HttpExchange ex) throws IOException {
        String sessionId = ex.getRequestHeaders().getFirst("X-Session-Id");
        if (sessionId == null || sessionId.isBlank()) {
            String cookie = ex.getRequestHeaders().getFirst("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String trimmed = part.trim();
                    if (trimmed.startsWith("SESSION_ID=")) {
                        sessionId = trimmed.substring("SESSION_ID=".length());
                        break;
                    }
                }
            }
        }
        if (sessionId == null || sessionId.isBlank()) {
            sendError(ex, 401, "No session");
            return null;
        }

        try {
            User u = authService.getUserBySession(sessionId);
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
            JsonUtils.writeJson(os, Map.of("error", msg));
        }
    }

    public static class CreateColumnRequest {
        public String title;
        public Integer position;
    }

    public static class UpdateColumnRequest {
        public String title;
        public Integer position;
    }
}






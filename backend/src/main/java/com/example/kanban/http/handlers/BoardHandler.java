package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.BoardView;
import com.example.kanban.model.User;
import com.example.kanban.service.AuthService;
import com.example.kanban.service.BoardService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class BoardHandler implements HttpHandler {

    private final AuthService authService;
    private final BoardService boardService;

    public BoardHandler(AuthService authService, BoardService boardService) {
        this.authService = authService;
        this.boardService = boardService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        try {
            String[] parts = path.split("/");
            if ("GET".equalsIgnoreCase(method)
                && parts.length == 4
                && "api".equals(parts[1])
                && "boards".equals(parts[2])
            ) {
                    long boardId = Long.parseLong(parts[3]);
                    handleGetBoard(exchange, boardId);
            } else if ("GET".equalsIgnoreCase(method)
                    && parts.length == 5
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
                    && "members".equals(parts[4])
            ) {
                long boardId = Long.parseLong(parts[3]);
                handleGetBoardMembers(exchange, boardId);

            }
            else {
                sendError(exchange, 404, "Not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleGetBoard(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            BoardView view = boardService.getBoardView(boardId, user.getId());

            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                JsonUtils.writeJson(os, view);
            }
        } catch (NoSuchElementException e) {
            sendError(ex, 404, "Board not found");
        } catch (IllegalAccessException e) {
            sendError(ex, 403, "Access denied");
        }
    }

    private void handleGetBoardMembers(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;
        try {
            List<User> members = boardService.getMembers(boardId);

            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                JsonUtils.writeJson(os, members);
            }
        } catch (NoSuchElementException e) {
            sendError(ex, 404, "Board not found");
        }
    }

    private User requireAuth(HttpExchange ex) throws IOException {
        String sessionId = extractSessionId(ex);
        if (sessionId == null) {
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

    private String extractSessionId(HttpExchange ex) {
        String sessionId = ex.getRequestHeaders().getFirst("X-Session-Id");
        if (sessionId != null && !sessionId.isBlank()) return sessionId;
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
}

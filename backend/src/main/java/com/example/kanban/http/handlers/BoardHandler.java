package com.example.kanban.http.handlers;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.Board;
import com.example.kanban.model.Column;
import com.example.kanban.model.Task;
import com.example.kanban.model.User;
import com.example.kanban.repository.BoardRepository;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class BoardHandler implements HttpHandler {

    private final AuthService authService;
    private final BoardRepository boardRepo;

    public BoardHandler(AuthService authService, ConnectionManager cm) {
        this.authService = authService;
        this.boardRepo = new BoardRepository(cm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath(); // /api/boards or /api/boards/{id}
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        try {
            if ("GET".equalsIgnoreCase(method)) {
                // /api/boards/{id}
                String[] parts = path.split("/");
                if (parts.length == 4) {
                    long boardId = Long.parseLong(parts[3]);
                    handleGetBoard(exchange, boardId);
                } else {
                    // тут можно сделать список досок GET /api/boards
                    sendError(exchange, 404, "Not found");
                }
            } else {
                sendError(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleGetBoard(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return; // ответ уже отправлен

        Board board = boardRepo.findBoardById(boardId);
        if (board == null) {
            sendError(ex, 404, "Board not found");
            return;
        }

        // TODO: проверка прав пользователя (owner или member) – отдельный репозиторий

        List<Column> columns = boardRepo.findColumnsByBoard(boardId);
        Map<Long, List<Task>> tasksByColumn = boardRepo.findTasksByBoardGrouped(boardId);

        // собираем DTO
        List<Map<String, Object>> columnsDto = new ArrayList<>();
        for (Column c : columns) {
            List<Task> tasks = tasksByColumn.getOrDefault(c.getId(), List.of());
            List<Map<String, Object>> tasksDto = new ArrayList<>();
            for (Task t : tasks) {
                Map<String, Object> taskDto = new LinkedHashMap<>();
                taskDto.put("id", t.getId());
                taskDto.put("title", t.getTitle());
                taskDto.put("description", t.getDescription());
                taskDto.put("position", t.getPosition());
                taskDto.put("columnId", t.getColumnId());
                taskDto.put("dueDate", t.getDueDate() != null ? t.getDueDate().toString() : null);
                // сюда же потом добавишь assignees, labels, participants
                tasksDto.add(taskDto);
            }

            Map<String, Object> columnDto = new LinkedHashMap<>();
            columnDto.put("id", c.getId());
            columnDto.put("title", c.getTitle());
            columnDto.put("position", c.getPosition());
            columnDto.put("tasks", tasksDto);

            columnsDto.add(columnDto);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", board.getId());
        response.put("title", board.getTitle());
        response.put("description", board.getDescription());
        response.put("columns", columnsDto);

        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, response);
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

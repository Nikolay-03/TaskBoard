package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.Board;
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
                && parts.length == 3
                && "api".equals(parts[1])
                && "boards".equals(parts[2])
            ) {
                handleGetBoards(exchange);
            } else if ("GET".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
                    && "favorites".equals(parts[3])
            ) {
                handleGetFavoriteBoards(exchange);
            } else if ("POST".equalsIgnoreCase(method)
                    && parts.length == 3
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
            ) {
                handleCreateBoard(exchange);
            } else if ("GET".equalsIgnoreCase(method)
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
            } else if ("PATCH".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
            ) {
                long boardId = Long.parseLong(parts[3]);
                handleUpdateBoard(exchange, boardId);
            } else if ("DELETE".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
            ) {
                long boardId = Long.parseLong(parts[3]);
                handleDeleteBoard(exchange, boardId);
            } else if ("POST".equalsIgnoreCase(method)
                    && parts.length == 5
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
                    && "favorite".equals(parts[4])
            ) {
                long boardId = Long.parseLong(parts[3]);
                handleAddFavorite(exchange, boardId);
            } else if ("DELETE".equalsIgnoreCase(method)
                    && parts.length == 5
                    && "api".equals(parts[1])
                    && "boards".equals(parts[2])
                    && "favorite".equals(parts[4])
            ) {
                long boardId = Long.parseLong(parts[3]);
                handleRemoveFavorite(exchange, boardId);
            }
            else {
                sendError(exchange, 404, "Not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleGetBoards(HttpExchange ex) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            List<Board> boards = boardService.getBoardsByMember(user.getId());

            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                JsonUtils.writeJson(os, boards);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, "Internal server error");
        }
    }

    private void handleGetFavoriteBoards(HttpExchange ex) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            List<Board> boards = boardService.getFavoriteBoards(user.getId());

            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                JsonUtils.writeJson(os, boards);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, "Internal server error");
        }
    }

    private void handleCreateBoard(HttpExchange ex) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            CreateBoardRequest req = JsonUtils.readJson(ex.getRequestBody(), CreateBoardRequest.class);
            if (req == null || req.title == null || req.title.isBlank()) {
                sendError(ex, 400, "Title is required");
                return;
            }

            Board board = boardService.createBoard(user.getId(), req.title, req.description, req.members);

            ex.sendResponseHeaders(201, 0);
            try (OutputStream os = ex.getResponseBody()) {
                JsonUtils.writeJson(os, board);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ex, 500, "Internal server error");
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
    private void handleUpdateBoard(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            UpdateBoardRequest req = JsonUtils.readJson(ex.getRequestBody(), UpdateBoardRequest.class);
            if (req == null) {
                sendError(ex, 400, "Invalid request");
                return;
            }

            BoardView existingBoardView = boardService.getBoardView(boardId, user.getId());
            String title = req.title != null ? req.title : existingBoardView.getTitle();
            String description = req.description != null ? req.description : existingBoardView.getDescription();

            Board updatedBoard = boardService.updateBoard(boardId, user.getId(), title, description);

            // Обновляем участников, если они переданы
            if (req.members != null) {
                boardService.updateBoardMembers(boardId, user.getId(), req.members);
            }

            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                JsonUtils.writeJson(os, updatedBoard);
            }
        } catch (NoSuchElementException e) {
            sendError(ex, 404, "Board not found");
        } catch (IllegalAccessException e) {
            sendError(ex, 403, "Access denied");
        } catch (IllegalArgumentException e) {
            sendError(ex, 400, e.getMessage());
        }
    }
    private void handleDeleteBoard(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            boardService.deleteBoard(boardId, user.getId());
            ex.sendResponseHeaders(204, -1);
        } catch (NoSuchElementException e) {
            sendError(ex, 404, "Board not found");
        } catch (IllegalAccessException e) {
            sendError(ex, 403, "Access denied");
        }
    }

    private void handleAddFavorite(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            boardService.addFavorite(boardId, user.getId());
            ex.sendResponseHeaders(204, -1);
        } catch (NoSuchElementException e) {
            sendError(ex, 404, "Board not found");
        } catch (IllegalAccessException e) {
            sendError(ex, 403, "Access denied");
        }
    }

    private void handleRemoveFavorite(HttpExchange ex, long boardId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        try {
            boardService.removeFavorite(boardId, user.getId());
            ex.sendResponseHeaders(204, -1);
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

    public static class CreateBoardRequest {
        public String title;
        public String description;
        public List<Long> members;
    }

    public static class UpdateBoardRequest {
        public String title;
        public String description;
        public List<Long> members;
    }
}

package com.example.kanban.http.handlers;

import com.example.kanban.http.JsonUtils;
import com.example.kanban.model.*;
import com.example.kanban.repository.ColumnRepository;
import com.example.kanban.repository.TaskAssigneeRepository;
import com.example.kanban.repository.TaskLabelRepository;
import com.example.kanban.repository.TaskParticipantRepository;
import com.example.kanban.repository.TaskRepository;
import com.example.kanban.service.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TaskHandler implements HttpHandler {

    private final AuthService authService;
    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final TaskAssigneeRepository assigneeRepository;
    private final TaskParticipantRepository participantRepository;
    private final TaskLabelRepository taskLabelRepository;

    public TaskHandler(AuthService authService,
                       TaskRepository taskRepository,
                       ColumnRepository columnRepository,
                       TaskAssigneeRepository assigneeRepository,
                       TaskParticipantRepository participantRepository,
                       TaskLabelRepository taskLabelRepository) {
        this.authService = authService;
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.assigneeRepository = assigneeRepository;
        this.participantRepository = participantRepository;
        this.taskLabelRepository = taskLabelRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        try {
            String[] parts = path.split("/");
            // варианты:
            // /api/columns/{columnId}/tasks
            // /api/tasks/{taskId}
            // /api/tasks/{taskId}/assignees
            // /api/tasks/{taskId}/assignees/{userId}
            // /api/tasks/{taskId}/participants[/{userId}]
            // /api/tasks/{taskId}/labels[/{labelId}]

            if ("POST".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "tasks".equals(parts[2])
            ) {
                long columnId = Long.parseLong(parts[3]);
                handleCreateTaskInColumn(exchange, columnId);

            } else if ("PATCH".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "tasks".equals(parts[2])) {
                long taskId = Long.parseLong(parts[3]);
                handleUpdateTask(exchange, taskId);

            } else if ("DELETE".equalsIgnoreCase(method)
                    && parts.length == 4
                    && "api".equals(parts[1])
                    && "tasks".equals(parts[2])) {
                long taskId = Long.parseLong(parts[3]);
                handleDeleteTask(exchange, taskId);

            }
            else {
                sendError(exchange, 404, "Not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    // ---------- создание задачи в столбце ----------

    private void handleCreateTaskInColumn(HttpExchange ex, long columnId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        Column column = columnRepository.findById(columnId);
        if (column == null) {
            sendError(ex, 404, "Column not found");
            return;
        }

        CreateTaskRequest req = JsonUtils.readJson(ex.getRequestBody(), CreateTaskRequest.class);
        if (req == null || req.title == null || req.title.isBlank()) {
            sendError(ex, 400, "Invalid request");
            return;
        }

        LocalDate dueDate = (req.dueDate != null && !req.dueDate.isBlank())
                ? LocalDate.parse(req.dueDate)
                : null;


        Task t = taskRepository.createTask(
                column.getBoardId(),
                columnId,
                req.title,
                req.description,
                dueDate
        );
        long taskId = t.getId();
        if (req.assignees != null) {
            for (long assigneeId : req.assignees) {
                assigneeRepository.addAssignee(taskId, assigneeId);
            }
            t.setAssignees(assigneeRepository.getAssigneesByTaskId(taskId));
        }
        if (req.labels != null) {
            for (long labelId : req.labels) {
                taskLabelRepository.addLabelToTask(taskId, labelId);
            }
            t.setLabels(taskLabelRepository.getLabelsByTaskId(taskId));
        }
        if (req.participants != null) {
            for (long participantId : req.participants) {
                participantRepository.addParticipant(taskId, participantId);
            }
            t.setParticipants(participantRepository.getParticipantsByTaskId(taskId));
        }
        ex.sendResponseHeaders(201, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, t);
        }
    }

    // ---------- обновление задачи ----------

    private void handleUpdateTask(HttpExchange ex, long taskId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        UpdateTaskRequest req = JsonUtils.readJson(ex.getRequestBody(), UpdateTaskRequest.class);
        if (req == null || req.columnId == null || req.title == null || req.title.isBlank() || req.position == null) {
            sendError(ex, 400, "Invalid request");
            return;
        }

        LocalDate dueDate = (req.dueDate != null && !req.dueDate.isBlank())
                ? LocalDate.parse(req.dueDate)
                : null;

        Task t = taskRepository.updateTask(
                taskId,
                req.columnId,
                req.title,
                req.description,
                req.position,
                dueDate
        );
        if (req.assignees != null) {
            for (long assigneeId : req.assignees) {
                assigneeRepository.addAssignee(taskId, assigneeId);
            }
            t.setAssignees(assigneeRepository.getAssigneesByTaskId(taskId));
        }
        if (req.labels != null) {
            for (long labelId : req.labels) {
                taskLabelRepository.addLabelToTask(taskId, labelId);
            }
            t.setLabels(taskLabelRepository.getLabelsByTaskId(taskId));
        }
        if (req.participants != null) {
            for (long participantId : req.participants) {
                participantRepository.addParticipant(taskId, participantId);
            }
            t.setParticipants(participantRepository.getParticipantsByTaskId(taskId));
        }
        if (t == null) {
            sendError(ex, 404, "Task not found");
            return;
        }

        ex.sendResponseHeaders(200, 0);
        try (OutputStream os = ex.getResponseBody()) {
            JsonUtils.writeJson(os, t);
        }
    }

    // ---------- удаление задачи ----------

    private void handleDeleteTask(HttpExchange ex, long taskId) throws Exception {
        User user = requireAuth(ex);
        if (user == null) return;

        taskRepository.deleteTask(taskId);
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

    // ---------- DTO ----------

    public static class CreateTaskRequest {
        public String title;
        public String description;
        public Integer position;
        public String dueDate; // yyyy-MM-dd
        public List<Long> labels;
        public List<Long> participants;
        public List<Long> assignees;
    }

    public static class UpdateTaskRequest {
        public Long columnId;
        public String title;
        public String description;
        public Integer position;
        public String dueDate;
        public List<Long> labels;
        public List<Long> participants;
        public List<Long> assignees;
    }

}

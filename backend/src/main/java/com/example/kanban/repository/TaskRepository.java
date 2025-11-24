package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private final ConnectionManager cm;

    public TaskRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public Task findById(long taskId) throws SQLException {
        String sql = """
            SELECT id, board_id, column_id, title, description, position,
                   due_date, created_at, updated_at
            FROM tasks
            WHERE id = ?
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Task t = new Task();
                t.setId(rs.getLong("id"));
                t.setBoardId(rs.getLong("board_id"));
                t.setColumnId(rs.getLong("column_id"));
                t.setTitle(rs.getString("title"));
                t.setDescription(rs.getString("description"));
                t.setPosition(rs.getInt("position"));
                java.sql.Date due = rs.getDate("due_date");
                if (due != null) t.setDueDate(due.toLocalDate());
                t.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                t.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                return t;
            }
        }
    }
    public List<Task> findByBoardId(long boardId) throws SQLException {
        String sql = """
            SELECT id, board_id, column_id, title, description, position,
                   due_date, created_at, updated_at
            FROM tasks
            WHERE board_id = ?
            ORDER BY column_id, position
            """;
        List<Task> list = new ArrayList<>();
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task t = new Task();
                    t.setId(rs.getLong("id"));
                    t.setBoardId(rs.getLong("board_id"));
                    t.setColumnId(rs.getLong("column_id"));
                    t.setTitle(rs.getString("title"));
                    t.setDescription(rs.getString("description"));
                    t.setPosition(rs.getInt("position"));
                    java.sql.Date due = rs.getDate("due_date");
                    if (due != null) t.setDueDate(due.toLocalDate());
                    t.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    t.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                    list.add(t);
                }
            }
        }
        return list;
    }
    public Task createTask(long boardId, long columnId, String title, String description,
                           int position, LocalDate dueDate) throws SQLException {
        String sql = """
            INSERT INTO tasks(board_id, column_id, title, description, position, due_date)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id, board_id, column_id, title, description, position,
                      due_date, created_at, updated_at
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, columnId);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setInt(5, position);
            if (dueDate != null) {
                ps.setDate(6, java.sql.Date.valueOf(dueDate));
            } else {
                ps.setNull(6, Types.DATE);
            }

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                Task t = new Task();
                t.setId(rs.getLong("id"));
                t.setBoardId(rs.getLong("board_id"));
                t.setColumnId(rs.getLong("column_id"));
                t.setTitle(rs.getString("title"));
                t.setDescription(rs.getString("description"));
                t.setPosition(rs.getInt("position"));
                java.sql.Date due = rs.getDate("due_date");
                if (due != null) t.setDueDate(due.toLocalDate());
                t.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                t.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                return t;
            }
        }
    }

    public Task updateTask(long taskId, long columnId, String title, String description,
                           int position, LocalDate dueDate) throws SQLException {
        String sql = """
            UPDATE tasks
            SET column_id = ?, title = ?, description = ?, position = ?, due_date = ?, updated_at = now()
            WHERE id = ?
            RETURNING id, board_id, column_id, title, description, position,
                      due_date, created_at, updated_at
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setInt(4, position);
            if (dueDate != null) {
                ps.setDate(5, java.sql.Date.valueOf(dueDate));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setLong(6, taskId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Task t = new Task();
                t.setId(rs.getLong("id"));
                t.setBoardId(rs.getLong("board_id"));
                t.setColumnId(rs.getLong("column_id"));
                t.setTitle(rs.getString("title"));
                t.setDescription(rs.getString("description"));
                t.setPosition(rs.getInt("position"));
                java.sql.Date due = rs.getDate("due_date");
                if (due != null) t.setDueDate(due.toLocalDate());
                t.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                t.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                return t;
            }
        }
    }

    public void deleteTask(long taskId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        }
    }
}

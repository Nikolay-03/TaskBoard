package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.Board;
import com.example.kanban.model.Column;
import com.example.kanban.model.Task;

import java.sql.*;
import java.time.ZoneId;
import java.util.*;

public class BoardRepository {
    private final ConnectionManager cm;

    public BoardRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public Board findBoardById(long boardId) throws SQLException {
        String sql = "SELECT id, owner_id, title, description, created_at, updated_at FROM boards WHERE id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Board b = new Board();
                b.setId(rs.getLong("id"));
                b.setOwnerId(rs.getLong("owner_id"));
                b.setTitle(rs.getString("title"));
                b.setDescription(rs.getString("description"));
                b.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                b.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                return b;
            }
        }
    }

    public List<Column> findColumnsByBoard(long boardId) throws SQLException {
        String sql = "SELECT id, board_id, title, position FROM columns WHERE board_id = ? ORDER BY position";
        List<Column> res = new ArrayList<>();
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Column c = new Column();
                    c.setId(rs.getLong("id"));
                    c.setBoardId(rs.getLong("board_id"));
                    c.setTitle(rs.getString("title"));
                    c.setPosition(rs.getInt("position"));
                    res.add(c);
                }
            }
        }
        return res;
    }

    public Map<Long, List<Task>> findTasksByBoardGrouped(long boardId) throws SQLException {
        String sql = """
            SELECT t.id, t.board_id, t.column_id, t.title, t.description, t.position,
                   t.due_date, t.created_at, t.updated_at
            FROM tasks t
            WHERE t.board_id = ?
            ORDER BY t.column_id, t.position
        """;
        Map<Long, List<Task>> map = new HashMap<>();
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getLong("id"));
                    task.setBoardId(rs.getLong("board_id"));
                    task.setColumnId(rs.getLong("column_id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setPosition(rs.getInt("position"));
                    java.sql.Date due = rs.getDate("due_date");
                    if (due != null) {
                        task.setDueDate(due.toLocalDate());
                    }
                    task.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    task.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());

                    map.computeIfAbsent(task.getColumnId(), k -> new ArrayList<>()).add(task);
                }
            }
        }
        return map;
    }
    public List<Task> findTasksByBoard(long boardId) throws SQLException {
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
                    Task task = new Task();
                    task.setId(rs.getLong("id"));
                    task.setBoardId(rs.getLong("board_id"));
                    task.setColumnId(rs.getLong("column_id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setPosition(rs.getInt("position"));

                    java.sql.Date due = rs.getDate("due_date");
                    if (due != null) {
                        task.setDueDate(due.toLocalDate());
                    }

                    task.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    task.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());

                    list.add(task);
                }
            }
        }
        return list;
    }

}

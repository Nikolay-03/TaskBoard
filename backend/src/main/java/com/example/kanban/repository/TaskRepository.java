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
                SELECT *
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

    private int getNextPosition(Connection con, long columnId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(position), 0) + 1 AS next_pos FROM tasks WHERE column_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("next_pos");
            }
        }
    }

    public Task createTask(long boardId, long columnId, String title, String description, LocalDate dueDate) throws SQLException {

        String sql = """
            INSERT INTO tasks (board_id, column_id, title, description, position, due_date)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id, board_id, column_id, title, description, position, created_at, updated_at, due_date
        """;

        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int nextPos = getNextPosition(con, columnId);

            ps.setLong(1, boardId);
            ps.setLong(2, columnId);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setInt(5, nextPos);
            if (dueDate != null) {
                ps.setDate(6, java.sql.Date.valueOf(dueDate));
            } else {
                ps.setNull(6, Types.DATE);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Failed to insert task");
                }
                Task t = new Task();
                t.setId(rs.getLong("id"));
                t.setBoardId(rs.getLong("board_id"));
                t.setColumnId(rs.getLong("column_id"));
                t.setTitle(rs.getString("title"));
                t.setDescription(rs.getString("description"));
                t.setPosition(rs.getInt("position"));
                t.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                t.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                java.sql.Date due = rs.getDate("due_date");
                if (due != null) {
                    t.setDueDate(due.toLocalDate());
                }
                return t;
            }
        }
    }

    public Task updateTask(long taskId, long columnId, String title, String description,
                           int position, LocalDate dueDate) throws SQLException {
        String selectSql = "SELECT column_id, position FROM tasks WHERE id = ?";
        
        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                Long oldColumnId = null;
                Integer oldPosition = null;

                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setLong(1, taskId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.commit();
                            return null;
                        }
                        oldColumnId = rs.getLong("column_id");
                        oldPosition = rs.getInt("position");
                    }
                }

                boolean columnChanged = !oldColumnId.equals(columnId);
                boolean positionChanged = !oldPosition.equals(position);

                if (columnChanged) {
                    String shiftOldColumnSql = "UPDATE tasks SET position = position - 1 WHERE column_id = ? AND position > ? AND id != ?";
                    try (PreparedStatement ps = con.prepareStatement(shiftOldColumnSql)) {
                        ps.setLong(1, oldColumnId);
                        ps.setInt(2, oldPosition);
                        ps.setLong(3, taskId);
                        ps.executeUpdate();
                    }

                    String shiftNewColumnSql = "UPDATE tasks SET position = position + 1 WHERE column_id = ? AND position >= ?";
                    try (PreparedStatement ps = con.prepareStatement(shiftNewColumnSql)) {
                        ps.setLong(1, columnId);
                        ps.setInt(2, position);
                        ps.executeUpdate();
                    }
                } else if (positionChanged) {
                    if (position > oldPosition) {
                        String shiftSql = "UPDATE tasks SET position = position - 1 WHERE column_id = ? AND position > ? AND position <= ? AND id != ?";
                        try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                            ps.setLong(1, columnId);
                            ps.setInt(2, oldPosition);
                            ps.setInt(3, position);
                            ps.setLong(4, taskId);
                            ps.executeUpdate();
                        }
                    } else {
                        String shiftSql = "UPDATE tasks SET position = position + 1 WHERE column_id = ? AND position >= ? AND position < ? AND id != ?";
                        try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                            ps.setLong(1, columnId);
                            ps.setInt(2, position);
                            ps.setInt(3, oldPosition);
                            ps.setLong(4, taskId);
                            ps.executeUpdate();
                        }
                    }
                }

                String updateSql = """
                        UPDATE tasks
                        SET column_id = ?, title = ?, description = ?, position = ?, due_date = ?, updated_at = now()
                        WHERE id = ?
                        RETURNING id, board_id, column_id, title, description, position,
                                  due_date, created_at, updated_at
                        """;
                Task t;
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
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
                        if (!rs.next()) {
                            con.rollback();
                            return null;
                        }
                        t = new Task();
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
                    }
                }

                con.commit();
                return t;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void deleteTask(long taskId) throws SQLException {
        String selectSql = "SELECT column_id, position FROM tasks WHERE id = ?";
        String deleteSql = "DELETE FROM tasks WHERE id = ?";
        String shiftSql  = "UPDATE tasks SET position = position - 1 WHERE column_id = ? AND position > ?";

        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                Long columnId = null;
                Integer position = null;

                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setLong(1, taskId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            columnId = rs.getLong("column_id");
                            position = rs.getInt("position");
                        }
                    }
                }

                if (columnId == null || position == null) {
                    con.commit();
                    return;
                }

                try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
                    ps.setLong(1, taskId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                    ps.setLong(1, columnId);
                    ps.setInt(2, position);
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

}

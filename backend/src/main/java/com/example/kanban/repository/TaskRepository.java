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
                return mapResultSetToTask(rs);
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
        List<Task> tasks = new ArrayList<>();
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        return tasks;
    }

    private int getNextPosition(Connection con, long columnId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(position), 0) + 1 AS next_pos FROM tasks WHERE column_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_pos");
                }
                return 1;
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
            int nextPosition = getNextPosition(con, columnId);

            ps.setLong(1, boardId);
            ps.setLong(2, columnId);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setInt(5, nextPosition);
            setDateParameter(ps, 6, dueDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Failed to insert task");
                }
                return mapResultSetToTask(rs);
            }
        }
    }

    public Task updateTask(long taskId, long columnId, String title, String description,
                           int position, LocalDate dueDate) throws SQLException {
        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                TaskPositionInfo positionInfo = getTaskPositionInfo(con, taskId);
                if (positionInfo == null) {
                    con.commit();
                    return null;
                }

                boolean columnChanged = !positionInfo.columnId.equals(columnId);
                boolean positionChanged = !positionInfo.position.equals(position);

                if (columnChanged) {
                    shiftPositionsOnColumnChange(con, positionInfo.columnId, positionInfo.position, columnId, position);
                } else if (positionChanged) {
                    shiftPositionsOnPositionChange(con, columnId, positionInfo.position, position, taskId);
                }

                Task updatedTask = performTaskUpdate(con, taskId, columnId, title, description, position, dueDate);
                con.commit();
                return updatedTask;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void deleteTask(long taskId) throws SQLException {
        String deleteSql = "DELETE FROM tasks WHERE id = ?";
        String shiftSql = "UPDATE tasks SET position = position - 1 WHERE column_id = ? AND position > ?";

        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                TaskPositionInfo positionInfo = getTaskPositionInfo(con, taskId);
                if (positionInfo == null) {
                    con.commit();
                    return;
                }

                try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
                    ps.setLong(1, taskId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                    ps.setLong(1, positionInfo.columnId);
                    ps.setInt(2, positionInfo.position);
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

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setBoardId(rs.getLong("board_id"));
        task.setColumnId(rs.getLong("column_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setPosition(rs.getInt("position"));
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDate());
        }
        
        task.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        task.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        return task;
    }

    private void setDateParameter(PreparedStatement ps, int index, LocalDate date) throws SQLException {
        if (date != null) {
            ps.setDate(index, Date.valueOf(date));
        } else {
            ps.setNull(index, Types.DATE);
        }
    }

    private TaskPositionInfo getTaskPositionInfo(Connection con, long taskId) throws SQLException {
        String sql = "SELECT column_id, position FROM tasks WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaskPositionInfo(rs.getLong("column_id"), rs.getInt("position"));
                }
                return null;
            }
        }
    }

    private void shiftPositionsOnColumnChange(Connection con, long oldColumnId, int oldPosition,
                                              long newColumnId, int newPosition) throws SQLException {
        String shiftOldColumnSql = "UPDATE tasks SET position = position - 1 WHERE column_id = ? AND position > ?";
        try (PreparedStatement ps = con.prepareStatement(shiftOldColumnSql)) {
            ps.setLong(1, oldColumnId);
            ps.setInt(2, oldPosition);
            ps.executeUpdate();
        }

        String shiftNewColumnSql = "UPDATE tasks SET position = position + 1 WHERE column_id = ? AND position >= ?";
        try (PreparedStatement ps = con.prepareStatement(shiftNewColumnSql)) {
            ps.setLong(1, newColumnId);
            ps.setInt(2, newPosition);
            ps.executeUpdate();
        }
    }

    private void shiftPositionsOnPositionChange(Connection con, long columnId, int oldPosition,
                                                int newPosition, long taskId) throws SQLException {
        if (newPosition > oldPosition) {
            String shiftSql = "UPDATE tasks SET position = position - 1 WHERE column_id = ? AND position > ? AND position <= ? AND id != ?";
            try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                ps.setLong(1, columnId);
                ps.setInt(2, oldPosition);
                ps.setInt(3, newPosition);
                ps.setLong(4, taskId);
                ps.executeUpdate();
            }
        } else {
            String shiftSql = "UPDATE tasks SET position = position + 1 WHERE column_id = ? AND position >= ? AND position < ? AND id != ?";
            try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                ps.setLong(1, columnId);
                ps.setInt(2, newPosition);
                ps.setInt(3, oldPosition);
                ps.setLong(4, taskId);
                ps.executeUpdate();
            }
        }
    }

    private Task performTaskUpdate(Connection con, long taskId, long columnId, String title,
                                   String description, int position, LocalDate dueDate) throws SQLException {
        String sql = """
                UPDATE tasks
                SET column_id = ?, title = ?, description = ?, position = ?, due_date = ?, updated_at = now()
                WHERE id = ?
                RETURNING id, board_id, column_id, title, description, position,
                          due_date, created_at, updated_at
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setInt(4, position);
            setDateParameter(ps, 5, dueDate);
            ps.setLong(6, taskId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapResultSetToTask(rs);
            }
        }
    }

    private static class TaskPositionInfo {
        final Long columnId;
        final Integer position;

        TaskPositionInfo(Long columnId, Integer position) {
            this.columnId = columnId;
            this.position = position;
        }
    }

}

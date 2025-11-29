package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.Column;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColumnRepository {

    private final ConnectionManager cm;

    public ColumnRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public List<Column> findByBoardId(long boardId) throws SQLException {
        String sql = """
            SELECT id, board_id, title, position
            FROM columns
            WHERE board_id = ?
            ORDER BY position
            """;
        List<Column> columns = new ArrayList<>();
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(mapResultSetToColumn(rs));
                }
            }
        }
        return columns;
    }

    public Column findById(long columnId) throws SQLException {
        String sql = """
            SELECT id, board_id, title, position
            FROM columns
            WHERE id = ?
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapResultSetToColumn(rs);
            }
        }
    }

    public Column createColumn(long boardId, String title, Integer position) throws SQLException {
        try (Connection con = cm.getConnection()) {
            int finalPosition = position != null ? position : getNextPosition(con, boardId);
            
            String sql = """
                INSERT INTO columns(board_id, title, position)
                VALUES (?, ?, ?)
                RETURNING id, board_id, title, position
                """;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, boardId);
                ps.setString(2, title);
                ps.setInt(3, finalPosition);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to insert column");
                    }
                    return mapResultSetToColumn(rs);
                }
            }
        }
    }

    private int getNextPosition(Connection con, long boardId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(position), 0) + 1 AS next_pos FROM columns WHERE board_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_pos");
                }
                return 1;
            }
        }
    }

    public Column updateColumn(long columnId, String title, Integer position) throws SQLException {
        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                ColumnPositionInfo positionInfo = getColumnPositionInfo(con, columnId);
                if (positionInfo == null) {
                    con.commit();
                    return null;
                }

                boolean positionChanged = position != null && !position.equals(positionInfo.position);

                if (positionChanged) {
                    shiftPositionsOnPositionChange(con, positionInfo.boardId, positionInfo.position, position, columnId);
                }

                Column updatedColumn = performColumnUpdate(con, columnId, title, position, positionInfo.position);
                con.commit();
                return updatedColumn;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private ColumnPositionInfo getColumnPositionInfo(Connection con, long columnId) throws SQLException {
        String sql = "SELECT board_id, position FROM columns WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ColumnPositionInfo(rs.getLong("board_id"), rs.getInt("position"));
                }
                return null;
            }
        }
    }

    private void shiftPositionsOnPositionChange(Connection con, long boardId, int oldPosition,
                                                int newPosition, long columnId) throws SQLException {
        if (newPosition > oldPosition) {
            String shiftSql = "UPDATE columns SET position = position - 1 WHERE board_id = ? AND position > ? AND position <= ? AND id != ?";
            try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                ps.setLong(1, boardId);
                ps.setInt(2, oldPosition);
                ps.setInt(3, newPosition);
                ps.setLong(4, columnId);
                ps.executeUpdate();
            }
        } else {
            String shiftSql = "UPDATE columns SET position = position + 1 WHERE board_id = ? AND position >= ? AND position < ? AND id != ?";
            try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                ps.setLong(1, boardId);
                ps.setInt(2, newPosition);
                ps.setInt(3, oldPosition);
                ps.setLong(4, columnId);
                ps.executeUpdate();
            }
        }
    }

    private Column performColumnUpdate(Connection con, long columnId, String title, Integer position,
                                      int currentPosition) throws SQLException {
        String sql = """
            UPDATE columns
            SET title = COALESCE(?, title),
                position = COALESCE(?, position)
            WHERE id = ?
            RETURNING id, board_id, title, position
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (title != null) {
                ps.setString(1, title);
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            if (position != null) {
                ps.setInt(2, position);
            } else {
                ps.setInt(2, currentPosition);
            }
            ps.setLong(3, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapResultSetToColumn(rs);
            }
        }
    }

    private Column mapResultSetToColumn(ResultSet rs) throws SQLException {
        Column column = new Column();
        column.setId(rs.getLong("id"));
        column.setBoardId(rs.getLong("board_id"));
        column.setTitle(rs.getString("title"));
        column.setPosition(rs.getInt("position"));
        return column;
    }

    private static class ColumnPositionInfo {
        final Long boardId;
        final Integer position;

        ColumnPositionInfo(Long boardId, Integer position) {
            this.boardId = boardId;
            this.position = position;
        }
    }

    public void deleteColumn(long columnId) throws SQLException {
        String deleteSql = "DELETE FROM columns WHERE id = ?";
        String shiftSql = "UPDATE columns SET position = position - 1 WHERE board_id = ? AND position > ?";

        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                ColumnPositionInfo positionInfo = getColumnPositionInfo(con, columnId);
                if (positionInfo == null) {
                    con.commit();
                    return;
                }

                try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
                    ps.setLong(1, columnId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(shiftSql)) {
                    ps.setLong(1, positionInfo.boardId);
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
}

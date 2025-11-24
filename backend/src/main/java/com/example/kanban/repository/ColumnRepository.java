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
        List<Column> list = new ArrayList<>();
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
                    list.add(c);
                }
            }
        }
        return list;
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
                Column c = new Column();
                c.setId(rs.getLong("id"));
                c.setBoardId(rs.getLong("board_id"));
                c.setTitle(rs.getString("title"));
                c.setPosition(rs.getInt("position"));
                return c;
            }
        }
    }

    public Column createColumn(long boardId, String title, int position) throws SQLException {
        String sql = """
            INSERT INTO columns(board_id, title, position)
            VALUES (?, ?, ?)
            RETURNING id, board_id, title, position
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setString(2, title);
            ps.setInt(3, position);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                Column c = new Column();
                c.setId(rs.getLong("id"));
                c.setBoardId(rs.getLong("board_id"));
                c.setTitle(rs.getString("title"));
                c.setPosition(rs.getInt("position"));
                return c;
            }
        }
    }

    public Column updateColumn(long columnId, String title, Integer position) throws SQLException {
        String sql = """
            UPDATE columns
            SET title = COALESCE(?, title),
                position = COALESCE(?, position)
            WHERE id = ?
            RETURNING id, board_id, title, position
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (title != null) {
                ps.setString(1, title);
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            if (position != null) {
                ps.setInt(2, position);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setLong(3, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Column c = new Column();
                c.setId(rs.getLong("id"));
                c.setBoardId(rs.getLong("board_id"));
                c.setTitle(rs.getString("title"));
                c.setPosition(rs.getInt("position"));
                return c;
            }
        }
    }

    public void deleteColumn(long columnId) throws SQLException {
        String sql = "DELETE FROM columns WHERE id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            ps.executeUpdate();
        }
    }
}

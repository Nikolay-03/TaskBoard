package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.Board;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardRepository {

    private final ConnectionManager cm;

    public BoardRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public List<Board> findBoardsByOwner(long ownerId) throws SQLException {
        String sql = """
            SELECT id, owner_id, title, description, created_at, updated_at
            FROM boards
            WHERE owner_id = ?
            ORDER BY id
            """;
        List<Board> list = new ArrayList<>();
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Board b = new Board();
                    b.setId(rs.getLong("id"));
                    b.setOwnerId(rs.getLong("owner_id"));
                    b.setTitle(rs.getString("title"));
                    b.setDescription(rs.getString("description"));
                    b.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    b.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                    list.add(b);
                }
            }
        }
        return list;
    }

    public Board findBoardById(long boardId) throws SQLException {
        String sql = """
            SELECT id, owner_id, title, description, created_at, updated_at
            FROM boards WHERE id = ?
            """;
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

    public Board createBoard(long ownerId, String title, String description) throws SQLException {
        String sql = """
            INSERT INTO boards(owner_id, title, description)
            VALUES (?, ?, ?)
            RETURNING id, owner_id, title, description, created_at, updated_at
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            ps.setString(2, title);
            ps.setString(3, description);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
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

    public Board updateBoard(long boardId, String title, String description) throws SQLException {
        String sql = """
            UPDATE boards
            SET title = ?, description = ?, updated_at = now()
            WHERE id = ?
            RETURNING id, owner_id, title, description, created_at, updated_at
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setLong(3, boardId);
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

    public void deleteBoard(long boardId) throws SQLException {
        String sql = "DELETE FROM boards WHERE id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.executeUpdate();
        }
    }
}

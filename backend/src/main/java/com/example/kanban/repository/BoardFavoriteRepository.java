package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoardFavoriteRepository {
    private final ConnectionManager cm;

    public BoardFavoriteRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public void addFavorite(long boardId, long userId) throws SQLException {
        String sql = """
            INSERT INTO board_favorites (board_id, user_id)
            VALUES (?, ?)
            ON CONFLICT (board_id, user_id) DO NOTHING
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void removeFavorite(long boardId, long userId) throws SQLException {
        String sql = "DELETE FROM board_favorites WHERE board_id = ? AND user_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public boolean isFavorite(long boardId, long userId) throws SQLException {
        String sql = "SELECT 1 FROM board_favorites WHERE board_id = ? AND user_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}


package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;

import java.sql.*;
import java.time.Instant;

public class SessionRepository {

    private final ConnectionManager cm;

    public SessionRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public void createSession(String sessionId, long userId, Instant expiresAt) throws SQLException {
        String sql = """
            INSERT INTO sessions(session_id, user_id, created_at, expires_at)
            VALUES (?, ?, now(), ?)
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setLong(2, userId);
            ps.setTimestamp(3, Timestamp.from(expiresAt));
            ps.executeUpdate();
        }
    }

    public Long findUserIdByValidSession(String sessionId) throws SQLException {
        String sql = """
            SELECT user_id
            FROM sessions
            WHERE session_id = ?
              AND expires_at > now()
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getLong("user_id");
            }
        }
    }

    public void deleteBySessionId(String sessionId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        }
    }

    public void deleteByUserId(long userId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE user_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}

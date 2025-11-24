package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Репозиторий для работы с таблицей sessions.
 */
public class SessionRepository {

    private final ConnectionManager cm;

    public SessionRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    /**
     * Создать новую сессию для пользователя.
     */
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

    /**
     * Найти userId по валидной (не протухшей) сессии.
     * Возвращает null, если сессия не найдена или истекла.
     */
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
                if (!rs.next()) {
                    return null;
                }
                return rs.getLong("user_id");
            }
        }
    }

    /**
     * Удалить одну сессию по sessionId (logout).
     */
    public void deleteBySessionId(String sessionId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE session_id = ?";

        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        }
    }

    /**
     * (опционально) Удалить все сессии пользователя, например при смене пароля.
     */
    public void deleteByUserId(long userId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE user_id = ?";

        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}

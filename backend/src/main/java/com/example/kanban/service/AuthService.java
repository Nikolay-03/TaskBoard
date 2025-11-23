package com.example.kanban.service;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.User;
import com.example.kanban.repository.UserRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepo;
    private final ConnectionManager cm;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserRepository userRepo, ConnectionManager cm) {
        this.userRepo = userRepo;
        this.cm = cm;
    }

    public User register(String email, String password, String name) throws Exception {
        String hash = hashPassword(password);
        return userRepo.createUser(email, hash, name);
    }

    public User login(String email, String password) throws Exception {
        User user = userRepo.findByEmail(email);
        if (user == null) return null;
        String hash = hashPassword(password);
        if (!hash.equals(user.getPasswordHash())) {
            return null;
        }
        return user;
    }

    public String createSession(long userId) throws SQLException {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        Instant now = Instant.now();
        Instant expires = now.plus(7, ChronoUnit.DAYS);

        String sql = "INSERT INTO sessions(session_id, user_id, created_at, expires_at) VALUES (?, ?, ?, ?)";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setLong(2, userId);
            ps.setTimestamp(3, Timestamp.from(now));
            ps.setTimestamp(4, Timestamp.from(expires));
            ps.executeUpdate();
        }
        return sessionId;
    }

    public User getUserBySession(String sessionId) throws SQLException {
        String sql = """
            SELECT u.id, u.email, u.password_hash, u.name
            FROM sessions s
            JOIN users u ON u.id = s.user_id
            WHERE s.session_id = ? AND s.expires_at > now()
        """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setName(rs.getString("name"));
                return u;
            }
        }
    }

    public void logout(String sessionId) throws SQLException {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        }
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes());
        return HexFormat.of().formatHex(digest);
    }
}

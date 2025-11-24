package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.User;

import java.sql.*;

public class UserRepository {

    private final ConnectionManager cm;

    public UserRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, email, password_hash, name, created_at FROM users WHERE email = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setName(rs.getString("name"));
                u.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                return u;
            }
        }
    }

    public User createUser(String email, String passwordHash, String name) throws SQLException {
        String sql = """
            INSERT INTO users(email, password_hash, name)
            VALUES (?, ?, ?)
            RETURNING id, email, password_hash, name, created_at
            """;
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, name);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setName(rs.getString("name"));
                u.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                return u;
            }
        }
    }

    public User findById(long id) throws SQLException {
        String sql = "SELECT id, email, password_hash, name, created_at FROM users WHERE id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setName(rs.getString("name"));
                u.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                return u;
            }
        }
    }
}

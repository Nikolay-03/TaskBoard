package com.example.kanban.service;

import com.example.kanban.model.User;
import com.example.kanban.repository.SessionRepository;
import com.example.kanban.repository.UserRepository;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.NoSuchElementException;
import java.util.UUID;

public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    // ---------- Регистрация ----------

    public User register(String email, String password, String name) throws Exception {
        // можно добавить валидацию email/пароля
        User existing = userRepository.findByEmail(email);
        if (existing != null) {
            throw new IllegalStateException("User with this email already exists");
        }

        String hash = hashPassword(password);
        return userRepository.createUser(email, hash, name);
    }

    // ---------- Логин ----------

    public User login(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }

        String hash = hashPassword(password);
        if (!hash.equals(user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    // ---------- Сессии ----------

    public String createSession(long userId) throws SQLException {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
        sessionRepository.createSession(sessionId, userId, expiresAt);
        return sessionId;
    }

    public User getUserBySession(String sessionId) throws SQLException {
        Long userId = sessionRepository.findUserIdByValidSession(sessionId);
        if (userId == null) return null;
        return userRepository.findById(userId);
    }

    public void logout(String sessionId) throws SQLException {
        sessionRepository.deleteBySessionId(sessionId);
    }

    // ---------- Внутренние хелперы ----------

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes());
        return HexFormat.of().formatHex(digest);
    }
}

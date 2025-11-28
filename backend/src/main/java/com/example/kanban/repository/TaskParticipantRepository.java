package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.User;

import java.sql.*;
import java.util.*;

public class TaskParticipantRepository {

    private final ConnectionManager cm;

    public TaskParticipantRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    public List<User> getParticipantsByTaskId(Long taskId) throws SQLException {
        List<User> result = new ArrayList<>();
        String sql = "SELECT * FROM task_participants JOIN users ON task_participants.user_id = users.id WHERE task_id = ?";
        try (Connection con = cm.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                result.add(user);
            }

        }
        return result;
    }

    public void addParticipant(long taskId, long userId) throws SQLException {
        String sql = "INSERT INTO task_participants(task_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void removeParticipant(long taskId, long userId) throws SQLException {
        String sql = "DELETE FROM task_participants WHERE task_id = ? AND user_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

}

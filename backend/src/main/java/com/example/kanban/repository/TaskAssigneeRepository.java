package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.User;

import java.sql.*;
import java.util.*;

/**
 * Ответственные за задачи (assignees):
 * task_assignees(task_id, user_id)
 */
public class TaskAssigneeRepository {

    private final ConnectionManager cm;

    public TaskAssigneeRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    /**
     * taskId -> список пользователей-ответственных.
     */
    public Map<Long, List<User>> findUsersByTaskIds(List<Long> taskIds) throws SQLException {
        Map<Long, List<User>> result = new HashMap<>();
        if (taskIds == null || taskIds.isEmpty()) {
            return result;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT ta.task_id,
                   u.id   AS user_id,
                   u.email,
                   u.name
            FROM task_assignees ta
            JOIN users u ON u.id = ta.user_id
            WHERE ta.task_id IN (
        """);
        for (int i = 0; i < taskIds.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("?");
        }
        sb.append(")");

        String sql = sb.toString();

        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = 1;
            for (Long id : taskIds) {
                ps.setLong(idx++, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long taskId = rs.getLong("task_id");
                    User u = new User();
                    u.setId(rs.getLong("user_id"));
                    u.setEmail(rs.getString("email"));
                    u.setName(rs.getString("name"));
                    // passwordHash не трогаем, оно нам в ответе не нужно

                    result.computeIfAbsent(taskId, k -> new ArrayList<>()).add(u);
                }
            }
        }

        return result;
    }

    // по желанию: методы addAssignee(taskId, userId), removeAssignee(taskId, userId)
}

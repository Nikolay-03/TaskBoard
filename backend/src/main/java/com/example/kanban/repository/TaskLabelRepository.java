package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.Label;

import java.sql.*;
import java.util.*;

/**
 * Лейблы задач:
 * labels(id, name, color)
 * task_labels(task_id, label_id)
 */
public class TaskLabelRepository {

    private final ConnectionManager cm;

    public TaskLabelRepository(ConnectionManager cm) {
        this.cm = cm;
    }

    /**
     * taskId -> список лейблов (Label).
     */
    public Map<Long, List<Label>> findLabelsByTaskIds(List<Long> taskIds) throws SQLException {
        Map<Long, List<Label>> result = new HashMap<>();
        if (taskIds == null || taskIds.isEmpty()) {
            return result;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("""
                    SELECT tl.task_id,
                           l.id,
                           l.name,
                           l.color
                    FROM task_labels tl
                    JOIN labels l ON l.id = tl.label_id
                    WHERE tl.task_id IN (
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
                    Label label = new Label();
                    label.setId(rs.getLong("id"));
                    label.setName(rs.getString("name"));
                    label.setColor(rs.getString("color"));

                    result.computeIfAbsent(taskId, k -> new ArrayList<>()).add(label);
                }
            }
        }

        return result;
    }

    public List<Label> getLabelsByTaskId(Long taskId) throws SQLException {
        List<Label> result = new ArrayList<>();
        String sql = "SELECT * FROM task_labels JOIN labels ON task_labels.label_id = labels.id WHERE task_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Label label = new Label();
                label.setId(rs.getLong("id"));
                label.setName(rs.getString("name"));
                label.setColor(rs.getString("color"));
                result.add(label);
            }
        }
        return result;
    }

    public void addLabelToTask(long taskId, long labelId) throws SQLException {
        String sql = "INSERT INTO task_labels(task_id, label_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, labelId);
            ps.executeUpdate();
        }
    }

    public void removeLabelFromTask(long taskId, long labelId) throws SQLException {
        String sql = "DELETE FROM task_labels WHERE task_id = ? AND label_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, labelId);
            ps.executeUpdate();
        }
    }

}

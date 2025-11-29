package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.Label;

import java.sql.*;
import java.util.*;

public class TaskLabelRepository {

    private final ConnectionManager cm;

    public TaskLabelRepository(ConnectionManager cm) {
        this.cm = cm;
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
    public void setLabelsForTask(long taskId, List<Long> labelIds) throws SQLException {
        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement deletePs = con.prepareStatement(
                        "DELETE FROM task_labels WHERE task_id = ?"
                )) {
                    deletePs.setLong(1, taskId);
                    deletePs.executeUpdate();
                }

                if (labelIds != null) {
                    try (PreparedStatement insertPs = con.prepareStatement(
                            "INSERT INTO task_labels (task_id, label_id) VALUES (?, ?)"
                    )) {
                        for (Long labelId : labelIds) {
                            insertPs.setLong(1, taskId);
                            insertPs.setLong(2, labelId);
                            insertPs.addBatch();
                        }
                        insertPs.executeBatch();
                    }
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
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
    public List<Label> findAll() throws SQLException {
        String sql = "SELECT id, name, color FROM labels ORDER BY id";
        List<Label> list = new ArrayList<>();

        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Label l = new Label();
                l.setId(rs.getLong("id"));
                l.setName(rs.getString("name"));
                l.setColor(rs.getString("color"));
                list.add(l);
            }
        }

        return list;
    }
}

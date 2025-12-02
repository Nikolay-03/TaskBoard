package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardMemberRepository {
    private final ConnectionManager cm;

    public BoardMemberRepository(ConnectionManager cm) {
        this.cm = cm;
    }
    public List<User> getMembersByBoardId(long boardId) throws SQLException {
        List<User> members = new ArrayList<>();
        String sql = "SELECT * FROM board_members INNER JOIN users ON board_members.user_id=users.id WHERE board_id=?";
        try(Connection con = cm.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1,boardId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                User member = new User();
                member.setId(rs.getLong("id"));
                member.setEmail(rs.getString("email"));
                member.setName(rs.getString("name"));
                member.setAvatar(rs.getString("avatar"));
                member.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                members.add(member);
            }
        }
        return members;
    }

    public boolean isMember(long boardId, long userId) throws SQLException {
        String sql = "SELECT 1 FROM board_members WHERE board_id = ? AND user_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void addMember(long boardId, long userId) throws SQLException {
        String sql = "INSERT INTO board_members (board_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void removeMember(long boardId, long userId) throws SQLException {
        String sql = "DELETE FROM board_members WHERE board_id = ? AND user_id = ?";
        try (Connection con = cm.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void setMembers(long boardId, List<Long> userIds) throws SQLException {
        String deleteSql = "DELETE FROM board_members WHERE board_id = ?";
        String insertSql = "INSERT INTO board_members (board_id, user_id) VALUES (?, ?)";
        
        try (Connection con = cm.getConnection()) {
            con.setAutoCommit(false);
            try {
                // Удаляем всех текущих участников
                try (PreparedStatement deletePs = con.prepareStatement(deleteSql)) {
                    deletePs.setLong(1, boardId);
                    deletePs.executeUpdate();
                }
                
                // Добавляем новых участников
                if (!userIds.isEmpty()) {
                    try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                        for (Long userId : userIds) {
                            insertPs.setLong(1, boardId);
                            insertPs.setLong(2, userId);
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
}

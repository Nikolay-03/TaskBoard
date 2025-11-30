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
}

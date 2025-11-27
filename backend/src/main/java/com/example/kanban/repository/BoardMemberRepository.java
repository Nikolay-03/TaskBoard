package com.example.kanban.repository;

import com.example.kanban.db.ConnectionManager;
import com.example.kanban.model.BoardMember;
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
    public List<BoardMember> getMembersByBoardId(long boardId) throws SQLException {
        List<BoardMember> members = new ArrayList<>();
        String sql = "SELECT * FROM board_members INNER JOIN users ON board_members.user_id=users.id WHERE board_id=?";
        try(Connection con = cm.getConnection(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1,boardId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                BoardMember member = new BoardMember();
                member.setBoardId(rs.getLong("board_id"));
                member.setUserId(rs.getLong("user_id"));
                member.setUserName(rs.getString("name"));
                member.setUserEmail(rs.getString("email"));
                members.add(member);
            }
        }
        return members;
    }
}

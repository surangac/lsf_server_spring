package com.dao.mapper;

import com.dfn.lsf.util.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/10/2015.
 */
public class AppCommentMapper implements org.springframework.jdbc.core.RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Comment obj = new Comment();
        obj.setReversedFrom(rs.getString("l21_reserved_from"));
        obj.setReversedTo(rs.getString("l21_reserved_to"));
        obj.setComment(rs.getString("l21_comment"));
        obj.setTimeStamp(rs.getDate("l21_date"));
        obj.setCommentID(rs.getString("l21_commented_id"));
        obj.setParentID(rs.getString("l21_parent_id"));
        obj.setCommentedBy(rs.getString("l21_commented_by"));
        return obj;
    }
}

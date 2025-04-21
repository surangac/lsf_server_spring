package com.dao.mapper;

import com.dfn.lsf.gbl.bo.UserAnswer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by manodyas on 8/26/2015.
 */
public class UserAnswerMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        UserAnswer obj = new UserAnswer();
        obj.setQuestionID(rs.getInt("a04_question_id"));
        obj.setAnswer(rs.getString("a04_answer"));
        obj.setIpAddress(rs.getString("a04_ip"));
        obj.setDate(rs.getString("a04_updated_date"));
        return obj;
    }
}

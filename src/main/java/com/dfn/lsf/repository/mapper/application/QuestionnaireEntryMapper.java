package com.dao.mapper.application;

import com.dfn.lsf.gbl.bo.application.QuestionnaireEntry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 16/3/2016.
 */
public class QuestionnaireEntryMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        QuestionnaireEntry questionnaireEntry = new QuestionnaireEntry();
        questionnaireEntry.setQuestionNumber(rs.getInt("m06_question_number"));
        questionnaireEntry.setAnswerNumber(rs.getInt("m06_configured_answer"));
        questionnaireEntry.setDescription(rs.getString("m06_content"));

        return questionnaireEntry;
    }
}

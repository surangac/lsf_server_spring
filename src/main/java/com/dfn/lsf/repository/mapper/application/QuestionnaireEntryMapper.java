package com.dfn.lsf.repository.mapper.application;

import com.dfn.lsf.model.application.QuestionnaireEntry;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 16/3/2016.
 */
public class QuestionnaireEntryMapper implements RowMapper<QuestionnaireEntry> {
    @Override
    public QuestionnaireEntry mapRow(ResultSet rs, int i) throws SQLException {
        QuestionnaireEntry questionnaireEntry = new QuestionnaireEntry();
        questionnaireEntry.setQuestionNumber(rs.getInt("m06_question_number"));
        questionnaireEntry.setAnswerNumber(rs.getInt("m06_configured_answer"));
        questionnaireEntry.setDescription(rs.getString("m06_content"));

        return questionnaireEntry;
    }
}

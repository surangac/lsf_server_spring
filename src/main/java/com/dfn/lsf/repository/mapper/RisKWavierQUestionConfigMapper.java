package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.responseMsg.RiskwavierQuestionConfig;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 3/15/2016.
 */
public class RisKWavierQUestionConfigMapper implements RowMapper<RiskwavierQuestionConfig> {
    @Override
    public RiskwavierQuestionConfig mapRow(ResultSet rs, int i) throws SQLException {
        RiskwavierQuestionConfig config = new RiskwavierQuestionConfig();
        config.setQuestionNumber(Integer.parseInt(rs.getString("m06_question_number")));
        config.setConfigureState(rs.getString("m06_configured_answer"));
        config.setContent(rs.getString("m06_content"));
        return config;
    }
}

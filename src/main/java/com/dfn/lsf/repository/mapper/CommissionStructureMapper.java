package com.dao.mapper;

import com.dfn.lsf.gbl.bo.CommissionStructure;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by surangac on 8/6/2015.
 */
public class CommissionStructureMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        CommissionStructure commissionStructure=new CommissionStructure();
        commissionStructure.setId(resultSet.getInt("m10_id"));
        commissionStructure.setFromValue(resultSet.getDouble("m10_from_value"));
        commissionStructure.setToValue(resultSet.getDouble("m10_to_value"));
        commissionStructure.setFlatAmount(resultSet.getDouble("m10_flat_amount"));
        commissionStructure.setPercentageAmount(resultSet.getDouble("m10_percentage"));
        commissionStructure.setSibourRate(resultSet.getDouble("m10_sibour_rate"));
        commissionStructure.setLibourRate(resultSet.getDouble("m10_libour_rate"));
        commissionStructure.setCreatedUserId(resultSet.getString("m10_created_user_id"));
        commissionStructure.setCreatedUserName(resultSet.getString("m10_created_user"));
        commissionStructure.setCreatedDate(resultSet.getString("m10_created_date"));
        return commissionStructure;
    }
}

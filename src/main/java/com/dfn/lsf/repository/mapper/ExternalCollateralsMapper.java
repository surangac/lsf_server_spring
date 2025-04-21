package com.dao.mapper;

import com.dfn.lsf.gbl.bo.ExternalCollaterals;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by surangac on 1/28/2016.
 */
public class ExternalCollateralsMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        ExternalCollaterals mapper=new ExternalCollaterals();
        mapper.setId(resultSet.getInt("l27_id"));
        mapper.setApplicationId(resultSet.getInt("l27_application_id"));
        mapper.setCollateralId(resultSet.getInt("l27_collateral_id"));
        mapper.setCollateralType(resultSet.getString("l27_collateral_type"));
        mapper.setReference(resultSet.getString("l27_reference"));
        mapper.setCollateralAmount(resultSet.getDouble("l27_collateral_amount"));
        mapper.setExpireDate(resultSet.getString("l27_expire_date"));
        mapper.setHaircutPercent(resultSet.getDouble("l27_haircut_percent"));
        mapper.setApplicableAmount(resultSet.getDouble("l27_applicable_amount"));
        mapper.setAddToCollateral(resultSet.getBoolean("l27_add_main_collateral"));
        mapper.setApprovedUserId(resultSet.getString("l27_approved_user_id"));
        return mapper;
    }
}
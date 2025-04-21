package com.dao.mapper;

import com.dfn.lsf.gbl.bo.LiquidityType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Nuskya on 7/16/2015.
 */
public class LiquidityTypeMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        LiquidityType obj = new LiquidityType();

        obj.setLiquidId(rs.getInt("L10_LIQUID_ID"));
        obj.setLiquidName(rs.getString("L10_LIQUID_NAME"));
        try{
            obj.setMarginabilityPercent(rs.getDouble("l17_marginability_perc"));
        }catch (Exception ex){

        }
        try{
            obj.setStockConcentrationPercent(rs.getDouble("l18_stock_concentrate_perce"));
        }catch (Exception ex){

        }
        return obj;
    }
}

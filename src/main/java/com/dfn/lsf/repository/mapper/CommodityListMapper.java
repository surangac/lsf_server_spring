package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Commodity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommodityListMapper implements RowMapper<Commodity> {
    @Override
    public Commodity mapRow(ResultSet resultSet, int i) throws SQLException {
        Commodity commodity = new Commodity();
        commodity.setSymbolName(resultSet.getString("m12_commodity_name"));
        commodity.setSymbolCode(resultSet.getString("m12_commodity_code"));
        commodity.setShortDescription(resultSet.getString("m12_description"));
        commodity.setExchange(resultSet.getString("m12_exchange"));
        commodity.setBroker(resultSet.getString("m12_broker"));
        commodity.setPrice(resultSet.getDouble("m12_price"));
        commodity.setUnitOfMeasure(resultSet.getString("m12_unit_of_measure"));
        commodity.setStatus(resultSet.getInt("m12_status"));
        try {
            commodity.setPercentage(resultSet.getDouble("l34_percentage"));
        }catch (Exception e){

        }
        try {
            commodity.setSoldAmnt(resultSet.getInt("l34_sold_amnt"));
        }catch (Exception e){

        }
        return commodity;
    }
}

package com.dao.mapper;

import com.dfn.lsf.gbl.bo.OrderProfit;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 9/16/2015.
 */
public class OrderProfitMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        OrderProfit orderProfit = new OrderProfit();
        orderProfit.setApplicationID(rs.getString("L23_APPLICATION_ID"));
        orderProfit.setOrderID(rs.getString("L23_ORDER_ID"));
        orderProfit.setDate(rs.getString("L23_DATE"));
        orderProfit.setProfitAmount(rs.getDouble("L23_PROFIT_AMT"));
        orderProfit.setCumulativeProfitAmount(rs.getDouble("L23_CUM_PROFIT_AMT"));
        return orderProfit;
    }
}

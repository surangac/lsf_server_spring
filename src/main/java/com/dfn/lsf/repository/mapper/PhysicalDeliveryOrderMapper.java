package com.dao.mapper;

import com.dfn.lsf.gbl.bo.PhysicalDeliverOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PhysicalDeliveryOrderMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        PhysicalDeliverOrder obj = new PhysicalDeliverOrder();

        obj.setClientName(resultSet.getString("L01_FULL_NAME"));
        obj.setApplicationId(resultSet.getString("L01_APP_ID"));
        obj.setPoId(resultSet.getString("L14_PURCHASE_ORD_ID"));
        obj.setIsReqForDelivery(resultSet.getInt("L14_PHYSICAL_DELIVERY"));
        obj.setRolloverId("0");
        if (resultSet.getString("L01_ROLLOVER_APP_ID") != null){
            obj.setRolloverId(resultSet.getString("L01_ROLLOVER_APP_ID"));
        }
        obj.setMobileNo(resultSet.getString("L01_MOBILE_NO"));
        obj.setOtherInfo(resultSet.getString("L14_CUSTOMER_COMMENT"));
        return obj;
    }
}

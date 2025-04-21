package com.dao.mapper;


import com.dfn.lsf.gbl.bo.MurabahaProduct;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MurabahaProductsMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        MurabahaProduct product = new MurabahaProduct();
        product.setProductType(resultSet.getInt("M07_TYPE"));
        product.setProfitMethod(resultSet.getString("M07_PROFIT_METHOD"));
        product.setProductName(resultSet.getString("M07_NAME"));
        product.setProductDescription(resultSet.getString("M07_DESCRIPTION"));
        product.setStatus(resultSet.getInt("M07_STATUS"));
        product.setProductNameAR(resultSet.getString("M07_AR_NAME"));
        product.setProductDescriptionAR(resultSet.getString("M07_AR_DESCRIPTION"));
        product.setFinanceMethodConfig(resultSet.getInt("M07_FINANCE_METHOD_CONFIG"));
        return product;
    }
}

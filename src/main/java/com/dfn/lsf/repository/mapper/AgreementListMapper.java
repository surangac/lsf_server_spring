package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Agreement;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AgreementListMapper implements RowMapper<Agreement> {
    @Override
    public Agreement mapRow(ResultSet resultSet, int i) throws SQLException {
        Agreement agreement = new Agreement();
        agreement.setProductType(resultSet.getInt("m11_product_type"));
        agreement.setFinanceMethod(resultSet.getInt("m11_finance_method"));
        agreement.setAgreementType(resultSet.getInt("m11_agreement_type"));
        agreement.setFileExtension(resultSet.getString("m11_file_extension"));
        agreement.setFileName(resultSet.getString("m11_file_name"));
        agreement.setVersion(resultSet.getInt("m11_version"));
        agreement.setFilePath(resultSet.getString("m11_file_path"));
        return agreement;
    }
}

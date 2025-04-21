package com.dao.mapper;

import com.dfn.lsf.gbl.bo.Documents;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by surangac on 8/20/2015.
 */
public class DocumentMasterMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Documents obj = new Documents();
        obj.setId(rs.getString("L03_DOC_ID"));
        obj.setDocumentName(rs.getString("L03_DOC_NAME"));
        obj.setRequired(rs.getBoolean("L03_IS_REQUIRED"));
        obj.setCreatedBy(rs.getString("L03_CREATED_BY"));
        obj.setCreatedDate(rs.getString("L03_CREATED_DATE"));
        obj.setApprovedBy(rs.getString("L03_LVL1_APPROVED_BY"));
        obj.setApprovedDate(rs.getString("L03_LVL1_APPROVED_DATE"));
        obj.setStatus(rs.getInt("L03_STATUS"));
        obj.setIsGlobal(rs.getInt("L03_IS_GLOBAL"));
        return obj;
    }
}

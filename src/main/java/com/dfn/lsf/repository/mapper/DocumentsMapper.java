package com.dao.mapper;

import com.dfn.lsf.gbl.bo.Documents;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class DocumentsMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Documents obj = new Documents();

        obj.setId(rs.getString("L03_DOC_ID"));
        obj.setDocumentName(rs.getString("L03_DOC_NAME"));
        obj.setOriginalFileName(rs.getString("L04_ORIG_FILE_NAME"));
        obj.setUploadedFileName(rs.getString("L04_UPLOADED_FILE_NAME"));
        obj.setPath(rs.getString("L04_PATH"));
        obj.setExtension(rs.getString("L04_EXTENTION"));
        obj.setUploadStatus(rs.getInt("L04_UPLOADED_STATUS"));
        obj.setRequired(rs.getBoolean("L03_IS_REQUIRED"));
        obj.setMimeType(rs.getString("L04_MIME_TYPE"));
        obj.setUploadedTime(rs.getString("L04_UPLOADED_TIME"));
        obj.setUploadedBy(rs.getString("L04_UPLOADED_USER_NAME"));
        obj.setUploadedLevel(String.valueOf(rs.getInt("L04_UPLOADED_LEVEL")));
        obj.setUploadedByUserID(rs.getString("L04_UPLOADED_USER_ID"));
        obj.setCreatedBy(rs.getString("L03_CREATED_BY"));
        obj.setCreatedDate(rs.getString("L03_CREATED_DATE"));
        obj.setApprovedBy(rs.getString("L03_LVL1_APPROVED_BY"));
        obj.setApprovedDate(rs.getString("L03_LVL1_APPROVED_DATE"));
        obj.setStatus(rs.getInt("L03_STATUS"));
        return obj;
    }
}

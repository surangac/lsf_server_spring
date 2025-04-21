package com.dao.mapper;

import com.dfn.lsf.gbl.bo.Documents;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MApplicationDocumentsMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Documents obj = new Documents();
        obj.setId(rs.getString("l04_l03_doc_id"));
        obj.setOriginalFileName(rs.getString("L04_ORIG_FILE_NAME"));
        obj.setUploadedFileName(rs.getString("L04_UPLOADED_FILE_NAME"));
        obj.setPath(rs.getString("L04_PATH"));
        obj.setExtension(rs.getString("L04_EXTENTION"));
        obj.setUploadStatus(rs.getInt("L04_UPLOADED_STATUS"));
        obj.setMimeType(rs.getString("L04_MIME_TYPE"));
        obj.setUploadedTime(rs.getString("L04_UPLOADED_TIME"));
        obj.setUploadedBy(rs.getString("L04_UPLOADED_USER_NAME"));
        obj.setUploadedLevel(String.valueOf(rs.getInt("L04_UPLOADED_LEVEL")));
        obj.setUploadedByUserID(rs.getString("L04_UPLOADED_USER_ID"));
        obj.setDocumentName(rs.getString("L03_DOC_NAME"));
        return obj;
    }
}

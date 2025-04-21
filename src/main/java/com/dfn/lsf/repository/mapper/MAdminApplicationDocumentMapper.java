package com.dao.mapper;

import com.dfn.lsf.gbl.bo.Documents;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/6/2015.
 */
public class MAdminApplicationDocumentMapper implements org.springframework.jdbc.core.RowMapper{
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Documents obj = new Documents();
        obj.setId(rs.getString("l19_id"));
        obj.setOriginalFileName(rs.getString("l19_orig_file_name"));
        obj.setUploadedFileName(rs.getString("l19_uploaded_file_name"));
        obj.setPath(rs.getString("l19_path"));
        obj.setExtension(rs.getString("l19_extention"));
        obj.setUploadStatus(rs.getInt("l19_uploaded_status"));
        obj.setUploadedTime(rs.getString("l19_uploaded_time"));
        obj.setUploadedByUserID(rs.getString("l19_uploaded_user_id"));
        obj.setUploadedBy(rs.getString("l19_uploaded_user_name"));
        obj.setUploadedLevel(rs.getString("l19_uploaded_level"));
        obj.setMimeType(rs.getString("l19_mime_type"));
        obj.setUploadedIP(rs.getString("l19_uploaded_ip"));
        return obj;
    }
}

package com.dao.mapper;

import com.dfn.lsf.gbl.bo.Status;
import com.dfn.lsf.gbl.bo.core.PurchaseOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/12/2015.
 */
public class StateFlowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Status obj = new Status();
        obj.setStatusId(rs.getInt("m02_approve_status"));
        obj.setStatusDescription(rs.getString("m02_state_description"));
        obj.setLevelId(rs.getInt("m02_state"));
        return obj;
    }
}

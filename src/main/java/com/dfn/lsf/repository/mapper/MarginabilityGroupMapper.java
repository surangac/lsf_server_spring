package com.dao.mapper;

import com.dfn.lsf.gbl.bo.MarginabilityGroup;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MarginabilityGroupMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        MarginabilityGroup obj = new MarginabilityGroup();

        obj.setId(rs.getString("l11_marginability_grp_id"));
        obj.setGroupName(rs.getString("l11_group_name"));
        obj.setCreatedDate(rs.getString("l11_created_date"));
        obj.setStatus(rs.getInt("l11_status"));
        obj.setCreatedBy(rs.getString("l11_created_by"));
        obj.setApprovedBy(rs.getString("l11_approved_by"));
        obj.setIsDefault(rs.getInt("l11_is_default"));
        obj.setGlobalMarginablePercentage(rs.getFloat("l11_global_marginability_perc"));

        try {
            if(rs.getString("l11_additional_details") != null) {
                obj.setAdditionalDetails(rs.getString("l11_additional_details"));
            }
        } catch(Exception e) {}
        //L12_L10_LIQUID_ID

        return obj;
    }
}

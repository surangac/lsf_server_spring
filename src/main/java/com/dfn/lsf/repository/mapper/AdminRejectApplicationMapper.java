package com.dao.mapper;

import com.dfn.lsf.gbl.bo.MurabahApplication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 3/5/2017.
 */
public class AdminRejectApplicationMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        MurabahApplication murabahApplication = new MurabahApplication();
        murabahApplication.setId(resultSet.getString("l01_app_id"));
        murabahApplication.setCustomerId(resultSet.getString("l01_customer_id"));
        murabahApplication.setProposalDate(resultSet.getString("l01_proposal_date"));
        murabahApplication.setMobileNo(resultSet.getString("l01_mobile_no"));
        murabahApplication.setPurchaseOrderId(resultSet.getInt("l14_purchase_ord_id"));
        murabahApplication.setStatusDescription(resultSet.getString("m02_state_description"));
        murabahApplication.setFullName(resultSet.getString("l01_full_name"));
        return murabahApplication;
    }
}

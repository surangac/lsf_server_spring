package com.dao.mapper;

import com.dfn.lsf.gbl.bo.responseMsg.OrderContractCustomerInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by manodyas on 6/9/2016.
 */
public class OrderContractUserInfoMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        OrderContractCustomerInfo info = new OrderContractCustomerInfo();
        info.setBranch(rs.getString("l01_bank_brch_name"));
        info.setBranchCity(rs.getString("l01_city"));
        info.setABICRepresented(rs.getString("l02_sts_changed_user_name"));
        info.setCustomerName(rs.getString("l01_full_name"));
        info.setCustomerID(rs.getString("l01_customer_id"));
        info.setPO(rs.getString("l01_pobox"));
        info.setPostCode(rs.getString("l01_zip_code"));
        info.setTelephoneNo(rs.getString("l01_telephone_no"));
        info.setEmail(rs.getString("l01_email"));
        info.setMobileNumber(rs.getString("l01_mobile_no"));
        if(rs.getString("l14_accepted_client_ip") != null){
          info.setOrderContractSignedIP(rs.getString("l14_accepted_client_ip"));
        }
        if(rs.getString("m01_c1_nin") != null ){
          info.setCustomerNIN(rs.getString("m01_c1_nin"));
        }

        if(rs.getDate("l14_customer_approve_date") != null){
            info.setContractSignDate(dateFormat.format(rs.getDate("l14_customer_approve_date")));
        }else{
           info.setContractSignDate(dateFormat.format(new Date()));
        }
        info.setSimaCharges(rs.getDouble("l14_sima_charges"));
        info.setTransferCharges(rs.getDouble("l14_transfer_charges"));
        info.setVatAmountforOrder(rs.getDouble("l14_vat_amount"));
        return info;
    }
}

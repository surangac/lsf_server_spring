package com.dao.mapper;

import com.dfn.lsf.gbl.bo.MurabahApplication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/28/2015.
 */
public class DocumentRelateAppsMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        MurabahApplication obj = new MurabahApplication();
        obj.setId(rs.getString("L01_APP_ID"));
        obj.setCustomerId(rs.getString("L01_CUSTOMER_ID"));
        obj.setFullName(rs.getString("L01_FULL_NAME"));
        obj.setOccupation(rs.getString("L01_OCCUPATION"));
        obj.setEmployer(rs.getString("L01_EMPLOYER"));
        obj.setSelfEmp(rs.getBoolean("L01_IS_SELF_EMP"));
        obj.setLineOfBusiness(rs.getString("L01_LINE_OF_BISINESS"));
        obj.setAvgMonthlyIncome(rs.getDouble("L01_AVG_MONTHLY_INCOME"));
        obj.setFinanceRequiredAmt(rs.getDouble("L01_FINANCE_REQ_AMT"));
        obj.setAddress(rs.getString("L01_ADDRESS"));
        obj.setMobileNo(rs.getString("L01_MOBILE_NO"));
        obj.setTeleNo(rs.getString("L01_TELEPHONE_NO"));
        obj.setEmail(rs.getString("L01_EMAIL"));
        obj.setFax(rs.getString("L01_FAX"));
        obj.setDibAcc(rs.getString("L01_DIB_ACC"));
        obj.setTradingAcc(rs.getString("L01_TRADING_ACC"));
        obj.setOtherBrkAvailable(rs.getBoolean("L01_IS_OTHER_BRK_AVAILABLE"));
        obj.setOtherBrkNames(rs.getString("L01_OTHER_BRK_NAMES"));
        obj.setOtherBrkAvgPf(rs.getString("L01_OTHER_BRK_AVG_PF"));
        obj.setOverallStatus(rs.getString("L01_OVERALL_STATUS"));
        obj.setCurrentLevel(rs.getInt("L01_CURRENT_LEVEL"));
        obj.setTypeofFacility(rs.getString("L01_TYPE_OF_FACILITY"));
        obj.setDate(rs.getString("L01_DATE"));
        obj.setFacilityType(rs.getString("L01_FACILITY_TYPE"));
        obj.setProposalDate(rs.getString("L01_PROPOSAL_DATE"));
        obj.setProposedLimit(rs.getDouble("L01_PROPOSAL_LIMIT"));
        obj.setReversedTo(rs.getString("L01_REVISED_TO"));
        obj.setReversedFrom(rs.getString("L01_REVISED_FROM"));
        obj.setStockConcentrationGroup(rs.getString("L01_L12_STOCK_CONC_GRP_ID"));
        obj.setMarginabilityGroup(rs.getString("L01_L11_MARGINABILITY_GRP_ID"));
        obj.setTenor(rs.getString("L01_L15_TENOR_ID"));
        obj.setInitialRAPV(rs.getDouble("L01_INITIAL_RAPV"));
        obj.setIsEditable(rs.getBoolean("L01_IS_EDITABLE"));
        obj.setIsReversed(rs.getBoolean("L01_IS_REVERSED"));
        obj.setIsEdited(rs.getBoolean("L01_IS_EDITED"));
        obj.setDocumentUploadedStatus(rs.getInt("L04_UPLOADED_STATUS"));
        return obj;
    }
}

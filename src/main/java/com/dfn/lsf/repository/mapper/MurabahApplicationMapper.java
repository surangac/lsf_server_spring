package com.dao.mapper;

import com.dfn.lsf.gbl.bo.MurabahApplication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MurabahApplicationMapper implements RowMapper {
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
        obj.setAvailableCashBalance(rs.getDouble("L01_CASH_BALANCE"));
        obj.setTradingAccExchange(rs.getString("L01_TRADING_ACC_EXCHANGE"));
        obj.setReviewDate(rs.getString("l01_review_date"));
        obj.setAdminFeeCharged(rs.getDouble("l01_admin_fee_charged"));
        obj.setMaximumNumberOfSymbols(rs.getInt("l01_max_symbol_cnt"));
        obj.setOtp(rs.getString("l01_otp"));
        obj.setOtpGeneratedTime(rs.getLong("l01_otp_generated_time"));
        obj.setCashAccount(rs.getString("l01_cash_acc"));
        obj.setCustomerReferenceNumber(rs.getString("l01_customer_ref_no"));
        obj.setZipCode(rs.getString("l01_zip_code"));
        obj.setBankBranchName(rs.getString("l01_bank_brch_name"));
        obj.setCity(rs.getString("l01_city"));
        obj.setPoBox(rs.getString("l01_pobox"));
        obj.setLsfAccountDeletionState(rs.getInt("l01_acc_closed_status"));
        obj.setCustomerActivityID(rs.getInt("l01_acc_activity_id"));
        /*try{
            rs.findColumn("m02_state_description");
            obj.setStatusDescription(rs.getString("m02_state_description"));
            if(rs.getString("l06_trading_acc_id") != null) {
                obj.setMlPortfolioNo(rs.getString("l06_trading_acc_id"));
            }
        }catch (SQLException e){

        }*/

        try{
            obj.setStatusDescription(rs.getString("m02_state_description"));
        }catch (Exception e){
            obj.setStatusDescription(null);
        }

        try{
            obj.setMlPortfolioNo(rs.getString("l06_trading_acc_id"));
        }catch (Exception e){
            obj.setMlPortfolioNo(null);
        }



        obj.setPreferedLanguage(rs.getString("l01_prefered_language"));
        obj.setDiscountOnProfit(rs.getInt("l01_discount_on_profit"));
        obj.setProfitPercentage(rs.getDouble("l01_profit_percentage"));
        obj.setAutomaticSettlementAllow(rs.getInt("l01_automatic_settlement"));
        obj.setProductType(rs.getInt("l01_product_type"));
        /*if(rs.getString("l01_last_profit_date") != null){
             obj.setLastProfitCycleDate(rs.getDate("l01_last_profit_date"));
             obj.setLastProfitCycleDateStr(LSFUtils.formatDateToString(obj.getLastProfitCycleDate()));
        }*/
        try{
            obj.setFinanceMethod(rs.getString("m11_finance_method"));
        }catch (Exception e){
            obj.setFinanceMethod(null);
        }
        try {
            obj.setRollOverAppId(rs.getString("l01_rollover_app_id"));
        }catch (Exception e){
            obj.setRollOverAppId("-1");
        }
        try {
            obj.setInvestorAcc(rs.getString("l07_investor_account"));
        }catch (Exception e){
            obj.setInvestorAcc("");
        }
        try {
            obj.setRollOverSeqNumber(rs.getInt("l01_rollover_count"));
        }catch (Exception e){
            obj.setRollOverSeqNumber(0);
        }

        return obj;

    }
}

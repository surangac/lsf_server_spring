package com.dao.mapper;

import com.dfn.lsf.gbl.bo.core.PurchaseOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nuskya on 7/20/2015.
 */
public class PurchaseOrderMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        PurchaseOrder obj = new PurchaseOrder();
        Date dateForSettlementDateDisplay = null;
        DateFormat fromFormat = new SimpleDateFormat("ddMMyyyy");
        DateFormat toFormat = new SimpleDateFormat("dd-MM-yyyy");
        obj.setId(rs.getString("L14_PURCHASE_ORD_ID"));
        try{
            obj.setCustomerName(rs.getString("L01_FULL_NAME"));
        }catch (Exception e){
          //  e.printStackTrace();
        }

        obj.setCustomerId(rs.getString("L14_CUSTOMER_ID"));
        try{
            obj.setTradingAccNum(rs.getString("L01_TRADING_ACC"));
        }catch (Exception e){
            //e.printStackTrace();
        }

        obj.setOrderValue(rs.getDouble("L14_ORD_VALUE"));
        obj.setOrderSettlementAmount(rs.getDouble("L14_ORD_SETTLEMENT_AMOUNT"));
        try {
            dateForSettlementDateDisplay = fromFormat.parse(rs.getString("L14_SETTLEMENT_DATE"));
          //  obj.setCustomerName(rs.getString("l01_full_name"));
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        try{
            obj.setSettlementDateDisplay(toFormat.format(dateForSettlementDateDisplay));
        }catch(Exception e){

        }

        obj.setCustomerApprovedDate(rs.getDate("L14_CUSTOMER_APPROVE_DATE"));
        obj.setSettlementDate(rs.getString("L14_SETTLEMENT_DATE"));
        obj.setSettlementDate(String.format("%08d", Integer.parseInt(obj.getSettlementDate())));
        obj.setProfitPercentage(rs.getDouble("L14_PROFIT_PERCENTAGE"));
        obj.setTradingAccount(rs.getString("L14_TRADING_ACCOUNT"));
        obj.setExchange(rs.getString("L14_EXCHANGE"));
        obj.setSettlementAccount(rs.getString("L14_SETTLEMENT_ACCOUNT"));
        obj.setOneTimeSettlement(rs.getBoolean("L14_IS_ONE_TIME_SETTLEMENT"));
        obj.setInstallmentFrequency(rs.getInt("L14_INSTALLMENT_FREQUENCY"));
        obj.setSettlementDurationInMonths(rs.getInt("L14_SET_DURATION_MONTHS"));
        obj.setSettlementStatus(rs.getInt("l14_settlement_status"));
        obj.setCreatedDate(rs.getString("L14_CREATED_DATE"));
        obj.setTenorId(rs.getString("L14_L15_TENOR_ID"));
        obj.setApplicationId(rs.getString("L14_APP_ID"));
        obj.setApprovalStatus(rs.getInt("L14_APPROVAL_STATUS"));
        obj.setProfitAmount(rs.getDouble("L14_PROFIT_AMOUNT"));
        obj.setSibourAmount(rs.getDouble("L14_SIBOUR_AMOUNT"));
        obj.setLibourAmount(rs.getDouble("L14_LIBOUR_AMOUNT"));
        obj.setApprovedByName(rs.getString("L14_APPROVED_BY_NAME"));
        obj.setApprovedDate(rs.getString("L14_APPROVED_DATE"));
        obj.setApprovedById(rs.getString("L14_APPROVED_BY_ID"));
        obj.setOrderStatus(rs.getInt("L14_ORD_STATUS"));
        obj.setOrderCompletedValue(rs.getDouble("L14_ORD_COMPLETED_VALUE"));
        obj.setCustomerApproveStatus(rs.getInt("l14_customer_approve_state"));
        obj.setNoOfCallingAttempts(rs.getInt("l14_no_of_calling_attempts"));
        obj.setLastCalledTime(rs.getString("l14_last_called_time"));
        obj.setSettledDate(rs.getString("l14_settled_date"));
        obj.setBasketTransferState(rs.getInt("l14_bskt_transfer_status"));
        obj.setSimaCharges(rs.getDouble("l14_sima_charges"));
        obj.setTransferCharges(rs.getDouble("l14_transfer_charges"));
        obj.setVatAmount(rs.getDouble("l14_vat_amount"));
        try {
            obj.setAuthAbicToSell(rs.getInt("l14_auth_abic_to_sell"));
//        obj.setSoldAmnt(rs.getInt("l34_sold_amnt"));
        }catch (Exception e) {

        }try {
            obj.setCashTransferStatus(rs.getInt("l07_status")); //0-Cash blocked,1-Block release,2-transfered
        }catch (Exception e){

        }
        try {
            obj.setInvestorAcc(rs.getString("l07_investor_account"));
        }catch (Exception ex){

        }
        try {
            obj.setIsPhysicalDelivery(rs.getInt("l14_physical_delivery"));
        }catch (Exception e){

        }
        try {
            obj.setSellButNotSettle(rs.getInt("l14_sell_but_not_settle"));
        }catch (Exception e){

        }
        try {
            obj.setCertificatePath(rs.getString("l14_com_certificate_path"));
        }catch (Exception e){

        }
        return obj;
    }
}

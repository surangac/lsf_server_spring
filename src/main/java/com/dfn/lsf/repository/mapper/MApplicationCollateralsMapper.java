package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.MApplicationCollaterals;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MApplicationCollateralsMapper implements RowMapper<MApplicationCollaterals> {
    @Override
    public MApplicationCollaterals mapRow(ResultSet rs, int i) throws SQLException {
        MApplicationCollaterals obj = new MApplicationCollaterals();
        obj.setId(rs.getString("L05_COLLATERAL_ID"));
        obj.setApplicationId(rs.getString("L05_L01_APP_ID"));
        obj.setNetTotalColleteral(rs.getDouble("L05_NET_TOTAL_COLLAT"));
        obj.setTotalCashColleteral(rs.getDouble("L05_TOTAL_CASH_COLLAT"));
        obj.setApprovedLimitAmount(rs.getDouble("L05_APPROVED_LIMIT_AMT"));
        obj.setUtilizedLimitAmount(rs.getDouble("L05_UTILIZED_LIMIT_AMT"));
        obj.setOpperativeLimitAmount(rs.getDouble("L05_OPERATIVE_LIMIT_AMT"));
        obj.setRemainingOperativeLimitAmount(rs.getDouble("L05_REM_OPERATIVE_LIMIT_AMT"));
        obj.setOutstandingAmount(rs.getDouble("L05_OUTSTANDING_AMT"));
        obj.setUpdatedDate(rs.getString("L05_UPDATED_DATE"));
        obj.setReadyForColleteralTransfer(rs.getBoolean("L05_IS_READY_FOR_COLLAT_TRANS"));
        obj.setFtv(rs.getDouble("L05_FTV"));
        obj.setFirstMargineCall(rs.getBoolean("L05_FIRST_MARGIN_CALL"));
        obj.setSecondMargineCall(rs.getBoolean("L05_SECOND_MARGIN_CALL"));
        obj.setLiqudationCall(rs.getBoolean("L05_LIQUIDATION_CALL"));
        obj.setTotalExternalColleteral(rs.getDouble("l05_total_external_collat"));
        obj.setTotalPFColleteral(rs.getDouble("l05_total_portfolio_collat"));
        obj.setMargineCallAtempts(rs.getInt("l05_margine_call_attempts"));
        obj.setBlockAmount(rs.getDouble("l05_block_amount"));
        obj.setMargineCallDate(rs.getString("l05_margine_call_date"));
        obj.setLiquidateCallDate(rs.getString("l05_liquidate_call_date"));
        obj.setInitialCashCollaterals(rs.getDouble("l05_initial_cash_collateral"));
        obj.setInitialPFCollaterals(rs.getDouble("l05_initial_pf_collateral"));

        try {
            obj.setIsExchangeAccountCreated(rs.getInt("l05_is_exchange_acc_created") == 1 ? true : false);

        } catch (Exception e) {

        }
        return obj;
    }
}

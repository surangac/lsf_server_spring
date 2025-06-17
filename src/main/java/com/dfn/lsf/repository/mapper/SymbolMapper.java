package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.LiquidityType;
import com.dfn.lsf.model.Symbol;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class SymbolMapper implements RowMapper<Symbol> {
    @Override
    public Symbol mapRow(ResultSet rs, int i) throws SQLException {
        Symbol obj = new Symbol();

        // obj.setSymbolCode(rs.getString("L20_SYMBOL_CODE"));
        // obj.setExchange(rs.getString("L20_EXCHANGE"));
        // obj.setPreviousClosed(rs.getDouble("L20_PREVIOUS_CLOSED"));
        //obj.setAvailableQty(rs.getInt("L20_AVAILABLE_QTY"));

        try {
            obj.setMarginabilityPercentage(rs.getDouble("marginability_percentage"));
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        obj.setSymbolCode(rs.getString("L08_SYMBOL_CODE"));
        obj.setExchange(rs.getString("L08_EXCHANGE"));
        if (rs.getString("L08_SHORT_DESC") == null) {
            obj.setShortDescription("");
        } else {
            obj.setShortDescription(rs.getString("L08_SHORT_DESC"));

        }
        obj.setPreviousClosed(rs.getDouble("L08_PREVIOUS_CLOSED"));
        obj.setAvailableQty(rs.getInt("L08_AVAILABLE_QTY"));
        obj.setMarketValue(rs.getDouble("l08_market_value"));
        try {
            obj.setColleteralQty(rs.getInt("l09_collat_qty"));
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        try {
            obj.setContibutionTocollateral(rs.getDouble("l09_contribution_to_collat"));
        } catch (Exception ex) {
           // ex.printStackTrace();
        }
        try {
            obj.setTransferedQty(rs.getInt("l09_transferred_qty"));

        } catch (Exception ex) {
           // ex.printStackTrace();
        }
        try {
            obj.setPercentage(rs.getDouble("l16_percentage"));
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        try {
            obj.setBlockedReference(rs.getString("L09_BLOCK_REFERENCE"));

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        try {
            obj.setTransStatus(rs.getInt("L09_STATUS"));

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        try {
            obj.setAvailableQty(rs.getInt("pl09_available_qty"));

        } catch (Exception ex) {
          //  ex.printStackTrace();
        }
        try {

            obj.setPreviousClosed(rs.getDouble("pl09_close_price"));


        } catch (Exception ex) {
          //  ex.printStackTrace();
        }
        try {

            obj.setLastTradePrice(rs.getDouble("pl09_ltp"));

        } catch (Exception ex) {
           // ex.printStackTrace();
        }
        LiquidityType concentrationType = new LiquidityType();
        try {
            concentrationType.setLiquidId(rs.getInt("l08_concentration_type"));

        } catch (Exception e) {
           // e.printStackTrace();
        }
        try {
            concentrationType.setLiquidName(rs.getString("conc_name"));
        } catch (Exception e) {
           // e.printStackTrace();
        }
        obj.setConcentrationType(concentrationType);
        LiquidityType liq = new LiquidityType();
        try {
            liq.setLiquidId(rs.getInt("L08_L10_LIQUID_ID"));
            liq.setLiquidName(rs.getString("l10_liquid_name"));
            obj.setLiquidName(liq.getLiquidName());

        } catch (Exception ex) {
           // ex.printStackTrace();
        }
        obj.setLiquidityType(liq);
        obj.setAllowedForCollateral(rs.getInt("l08_allowed_for_collateral"));
        obj.setLastTradePrice(rs.getDouble("l08_ltp"));
        try {
            obj.setInstrumentType(rs.getInt("L08_INSTRUMENT_TYPE"));
        }catch (Exception e){
            obj.setInstrumentType(-1);
        }
        try {
            obj.setSecurityType(rs.getString("L08_SECURITY_TYPE"));
        }catch (Exception e){
            obj.setSecurityType("");
        }
        try {
            if (rs.getString("l08_short_desc_ar") == null) {
                obj.setShortDescriptionAR("");
            } else {
                obj.setShortDescriptionAR(rs.getString("l08_short_desc_ar"));

            }
        } catch (Exception ex) {
         //   ex.printStackTrace();
        }
        obj.setAllowedForPo(rs.getInt("l08_allowed_for_po"));

        return obj;
    }
}

package com.dao.mapper;

import com.dfn.lsf.gbl.bo.report.MarginInformation;
import com.dfn.lsf.util.LSFUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 10/29/2015.
 */
public class MarginInformationReportMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        MarginInformation  marginInformation = new MarginInformation();
        marginInformation.setNumberOfCustomers(rs.getString("noofclient"));
        marginInformation.setMarginCommitment(String.valueOf(LSFUtils.roundUpDouble(rs.getString("margincommitment"))));
        marginInformation.setOutStandingBalance(String.valueOf(LSFUtils.roundUpDouble(rs.getString("outstandingbalance"))));
        marginInformation.setTotalPFMarketValue(String.valueOf(LSFUtils.roundUpDouble(rs.getString("totalpfmktvalue"))));
        marginInformation.setWeightedTotalPFMarketValue(String.valueOf(LSFUtils.roundUpDouble(rs.getString("totalpfmktvalue_w"))));
        marginInformation.setClientContributionToMarketValue(String.valueOf(LSFUtils.roundUpDouble(rs.getString("clientcontributintomktvalue"))));
        marginInformation.setWeightedClientContributionToMarketValue(String.valueOf(LSFUtils.roundUpDouble(rs.getString("clientcontributintomktvalue_w"))));
        marginInformation.setMarginRatio(String.valueOf(LSFUtils.roundUpDouble(rs.getString("marginratio"))));
        marginInformation.setWeightedRatio(String.valueOf(LSFUtils.roundUpDouble(rs.getString("marginratio_w"))));
        marginInformation.setNumberOfMarginCalls(rs.getString("noofmargincalls"));
        marginInformation.setNumberOfLiquidationInstructions(rs.getString("noofliqudationcalls"));
        marginInformation.setLiquidatedAmount(String.valueOf(LSFUtils.roundUpDouble("156.56")));
        return  marginInformation;
    }


}

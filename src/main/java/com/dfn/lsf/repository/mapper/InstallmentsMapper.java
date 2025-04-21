package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Installments;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by surangac on 8/12/2015.
 */
public class InstallmentsMapper implements RowMapper<Installments> {
    @Override
    public Installments mapRow(ResultSet resultSet, int i) throws SQLException {
        Installments obj = new Installments();
        obj.setInstallmentAmount(resultSet.getDouble("L22_INSTALLMENT_AMOUNT"));
        obj.setInstallmentCompletedDate(resultSet.getString("L22_INSTALLMENT_COMPLETED_DATE"));
        obj.setInstallmentStatus(resultSet.getInt("L22_INSTALLMENT_STATUS"));
        obj.setInstalmentDate(resultSet.getInt("L22_INSTALLMENT_DATE"));
        obj.setInstalmentNumber(resultSet.getInt("L22_INSTALLMENT_NUMBER"));
        obj.setOrderId(resultSet.getString("L22_PURCHASE_ORD_ID"));
        obj.setInstallmentDateString(formatSettlementDate(String.valueOf(resultSet.getInt("L22_INSTALLMENT_DATE"))));

        try{
            obj.setApplicationID(resultSet.getString("l14_app_id"));
        }
        catch (Exception e){

        }
        return obj;
    }

    private String formatSettlementDate(String settlementDate) {
        String formattedDate = "";
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sm = new SimpleDateFormat("MM/dd/yyyy");
        int difference = 0;
        try {
            Date settlement = df.parse(settlementDate);
            formattedDate = sm.format(settlement);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}

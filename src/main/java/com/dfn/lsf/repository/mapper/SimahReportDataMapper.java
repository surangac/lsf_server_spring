package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.report.SimahReportResponseDto;
import com.dfn.lsf.util.LSFUtils;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SimahReportDataMapper implements RowMapper<SimahReportResponseDto> {

    @Override
    public SimahReportResponseDto mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        SimahReportResponseDto simahResponseDto = new SimahReportResponseDto();
        simahResponseDto.setCreditInstrumentNumber(rs.getString("l01_app_id"));
        simahResponseDto.setIssueDate(LSFUtils.convertDateFormat(rs.getString("L01_DATE").toString(), "yyyy-MM-dd HH:mm:ss","dd/MM/yyyy"));
        simahResponseDto.setProductType("MGLD");
        simahResponseDto.setProductLimit(BigDecimal.valueOf(rs.getDouble("L01_FINANCE_REQ_AMT")));
        simahResponseDto.setSalaryAssignmentFlag("N");
        simahResponseDto.setProductStatus("C");
        simahResponseDto.setInstalmentAmount(BigDecimal.valueOf(rs.getDouble("L14_ORD_SETTLEMENT_AMOUNT")));
        simahResponseDto.setAverageInstalmentAmount(BigDecimal.valueOf(rs.getDouble("L14_ORD_SETTLEMENT_AMOUNT")));
        simahResponseDto.setPaymentFrequency("O");
        simahResponseDto.setTenure(rs.getInt("L01_L15_TENOR_ID"));
        simahResponseDto.setSecurityType("SH");
        simahResponseDto.setSubProductType("NAPP");
        simahResponseDto.setContractNumber(rs.getString("l01_app_id"));
        simahResponseDto.setNumberOfCreditInstrumentHolders(1);
        simahResponseDto.setCycleId(LSFUtils.convertDateFormat(rs.getString("L01_DATE").toString(), "yyyy-MM-dd HH:mm:ss","yyyyMMdd"));
        double settlementStatus = rs.getDouble("L14_SETTLEMENT_STATUS");
        if (settlementStatus > 0) {
            simahResponseDto.setPaymentStatus("O");
        } else {
            simahResponseDto.setPaymentStatus("P");
        }
        simahResponseDto.setOutstandingBalance(BigDecimal.valueOf(rs.getDouble("L14_ORD_SETTLEMENT_AMOUNT")));
        simahResponseDto.setAsOfDate(LocalDate.now().toString());
        simahResponseDto.setIdType("T");
        simahResponseDto.setConsumerId("1046153928");
        simahResponseDto.setMaritalStatus("M");
        simahResponseDto.setNationalityCode("SAU");
        simahResponseDto.setFullNameEnglish(rs.getString("L01_FULL_NAME"));
        simahResponseDto.setApplicantType("P");

        return simahResponseDto;
    }
}

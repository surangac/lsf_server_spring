package com.dao.mapper.reports;

import com.dfn.lsf.gbl.bo.report.ReportConfiguration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 10/2/2016.
 */
public class ReportConfigMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ReportConfiguration reportConfig = new ReportConfiguration();
        reportConfig.setReportId(rs.getInt("m04_report_id"));
        reportConfig.setReportName(rs.getString("m04_report_name"));
        reportConfig.setReportDescription(rs.getString("m04_report_description"));
        reportConfig.setPackageName(rs.getString("m04_package_name"));
        reportConfig.setDataProcedure(rs.getString("m04_data_proc"));
        reportConfig.setDataParameters(rs.getString("m04_data_params"));
        reportConfig.setParamProcedure(rs.getString("m04_param_proc"));
        reportConfig.setParamParameters(rs.getString("m04_param_params"));
        reportConfig.setClassName(rs.getString("m04_class_name"));
        reportConfig.setTemplatePath(rs.getString("m04_template_path"));
        reportConfig.setReportDestination(rs.getString("m04_report_destination"));

        return reportConfig;
    }
}

package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.ReportConfigObject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by manodyas on 10/20/2015.
 */
public class ReportConfigObjectMapper implements RowMapper<ReportConfigObject> {
    @Override
    public ReportConfigObject mapRow(ResultSet rs, int i) throws SQLException {
        ReportConfigObject reportConfigObject = new ReportConfigObject();
        reportConfigObject.setReportID(rs.getInt("m03_report_id"));
        reportConfigObject.setReportName(rs.getString("m03_report_name"));
        reportConfigObject.setFormat(rs.getString("m03_format"));
        reportConfigObject.setTemplatePath(rs.getString("m03_template_path"));
        reportConfigObject.setReportDestination(rs.getString("m03_report_destination"));
        String parameters =  rs.getString("m03_report_parameters");
        String[] parameterArray = parameters.split(",");
        ArrayList<String> parameterList = new ArrayList<>();
        for(int j = 0; j < parameterArray.length; j++){
               parameterList.add(parameterArray[j]);
        }
        reportConfigObject.setParameters(parameterList);
        String functionPara =  rs.getString("m03_report_function_para");
        String[] functionParaArray = functionPara.split(",");
        ArrayList<String> functionParaList = new ArrayList<>();
        for(int j = 0; j < functionParaArray.length; j++){
            functionParaList.add(functionParaArray[j]);
        }
        reportConfigObject.setFunctionVariables(functionParaList);
        return reportConfigObject;
    }
}

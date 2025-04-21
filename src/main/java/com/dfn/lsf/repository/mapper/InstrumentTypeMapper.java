package com.dao.mapper;

import com.dfn.lsf.gbl.bo.InstumentType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstrumentTypeMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        InstumentType obj = new InstumentType();
        obj.setInstrumentType(rs.getInt("L08_INSTRUMENT_TYPE"));
        obj.setSecurityType(rs.getString("L08_SECURITY_TYPE"));
        return obj;
    }
}

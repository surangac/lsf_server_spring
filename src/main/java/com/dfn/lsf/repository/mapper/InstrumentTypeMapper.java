package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.InstumentType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstrumentTypeMapper implements RowMapper<InstumentType> {
    @Override
    public InstumentType mapRow(ResultSet rs, int i) throws SQLException {
        InstumentType obj = new InstumentType();
        obj.setInstrumentType(rs.getInt("L08_INSTRUMENT_TYPE"));
        obj.setSecurityType(rs.getString("L08_SECURITY_TYPE"));
        return obj;
    }
}

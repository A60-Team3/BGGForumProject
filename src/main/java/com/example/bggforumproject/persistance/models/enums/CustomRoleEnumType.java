package com.example.bggforumproject.persistance.models.enums;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CustomRoleEnumType implements UserType<RoleType> {
    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<RoleType> returnedClass() {
        return RoleType.class;
    }

    @Override
    public boolean equals(RoleType x, RoleType y) {
        return x == y;
    }

    @Override
    public int hashCode(RoleType x) {
        return x.hashCode();
    }

    @Override
    public RoleType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String name = rs.getString(position);
        return name != null ? RoleType.valueOf(name) : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, RoleType value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value != null) {
            st.setString(index, ((RoleType) value).name());
        } else {
            st.setNull(index, Types.VARCHAR);
        }
    }

    @Override
    public RoleType deepCopy(RoleType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(RoleType value) {
        return value;
    }

    @Override
    public RoleType assemble(Serializable cached, Object owner) {
        return (RoleType) cached;
    }
}

package com.authservice.repository;

import com.authservice.model.UserRole;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {

    private final NamedParameterJdbcOperations operations;

    public UserRoleRepository(
        final NamedParameterJdbcOperations operations
    ) {
        this.operations = operations;
    }

    public UserRole fetchRoleByName(final String roleName) {
        String sql = "SELECT id, name"
            + " FROM rentcalculator.user_roles"
            + " WHERE name = :roleName";
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("roleName", roleName);

        return operations.queryForObject(sql, parameters, (rs, rowNum) -> new UserRole()
            .setId(rs.getInt("id"))
            .setName(rs.getString("name"))
        );
    }


}

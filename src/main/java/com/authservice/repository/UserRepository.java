package com.authservice.repository;

import com.authservice.model.User;
import com.authservice.model.UserRole;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final NamedParameterJdbcOperations operations;

    public UserRepository(
        final NamedParameterJdbcOperations operations
    ) {
        this.operations = operations;
    }

    public List<User> fetchAllUsers() {
        final String sql = "SELECT u.id, u.first_name, u.second_name, u.email, u.login,"
            + " u.role_id, ur.name as role_name, u.is_deleted, u.is_active, u.has_privileges"
            + " FROM rentcalculator.users u"
            + " JOIN rentcalculator.user_roles ur ON u.role_id = ur.id"
            + " WHERE u.is_deleted = FALSE";
        final MapSqlParameterSource parameters = new MapSqlParameterSource();

        return operations.query(sql, parameters, (rs, rowNum) -> new User()
            .setId(rs.getLong("id"))
            .setFirstName(rs.getString("first_name"))
            .setSecondName(rs.getString("second_name"))
            .setEmail(rs.getString("email"))
            .setLogin(rs.getString("login"))
            .setHasPrivileges(rs.getBoolean("has_privileges"))
            .setRole(new UserRole()
                .setId(rs.getInt("role_id"))
                .setName(rs.getString("role_name")))
            .setDeleted(rs.getBoolean("is_deleted"))
            .setActive(rs.getBoolean("is_active"))
        );
    }

    public User fetchUserByLogin(final String login) {
        final String sql = "SELECT u.id, u.first_name, u.second_name, u.email, u.has_privileges,"
                + " u.login, u.role_id, ur.name as role_name, u.password, u.is_deleted, u.is_active"
                + " FROM rentcalculator.users u"
                + " JOIN rentcalculator.user_roles ur ON u.role_id = ur.id"
                + " WHERE u.is_deleted = FALSE"
                + "   AND u.is_active = TRUE"
                + "   AND (u.login = :login OR u.email = :login)";
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("login", login);

        return operations.queryForObject(sql, parameters, (rs, rowNum) -> new User()
            .setId(rs.getLong("id"))
            .setFirstName(rs.getString("first_name"))
            .setSecondName(rs.getString("second_name"))
            .setEmail(rs.getString("email"))
            .setLogin(rs.getString("login"))
            .setHasPrivileges(rs.getBoolean("has_privileges"))
            .setRole(new UserRole()
                    .setId(rs.getInt("role_id"))
                    .setName(rs.getString("role_name")))
            .setPassword(rs.getString("password"))
            .setDeleted(rs.getBoolean("is_deleted"))
            .setActive(rs.getBoolean("is_active"))
        );
    }

    public User fetchUserById(final Long userId) {
        final String sql = "SELECT u.id, u.first_name, u.second_name, u.email,"
                + " u.login, u.role_id, ur.name AS role_name, u.is_deleted, u.is_active, u.has_privileges"
                + " FROM rentcalculator.users u"
                + " JOIN rentcalculator.user_roles ur ON u.role_id = ur.id"
                + " WHERE u.is_deleted = FALSE"
                + "   AND u.is_active = TRUE"
                + "   AND u.id = :userId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userId", userId);

        return operations.queryForObject(sql, parameters, (rs, rowNum) -> new User()
            .setId(rs.getLong("id"))
            .setFirstName(rs.getString("first_name"))
            .setSecondName(rs.getString("second_name"))
            .setEmail(rs.getString("email"))
            .setLogin(rs.getString("login"))
            .setHasPrivileges(rs.getBoolean("has_privileges"))
            .setRole(new UserRole()
                    .setId(rs.getInt("role_id"))
                    .setName(rs.getString("role_name")))
            .setDeleted(rs.getBoolean("is_deleted"))
            .setActive(rs.getBoolean("is_active"))
        );
    }

    public long createUser(final User user) {
        final String sql = "INSERT INTO"
            + " rentcalculator.users(login, password, role_id)"
            + " VALUES (:login, :pass, :roleId)";
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("login", user.getLogin())
            .addValue("pass", user.getPassword())
            .addValue("roleId", user.getRole().getId());

        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        operations.update(sql, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void updateUserInformation(
        final String firstName, final String secondName,
        final String email, final Long userId
    ) {
        final String sql = "UPDATE rentcalculator.users u"
            + " SET u.first_name = :firstName,"
            + "     u.second_name = :secondName,"
            + "     u.email = :email"
            + " WHERE u.id = :userId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("firstName", firstName)
            .addValue("secondName", secondName)
            .addValue("email", email);

        operations.update(sql, parameters);
    }

    public void deleteUser(final Long userId) {
        final String sql = "UPDATE rentcalculator.users u"
            + " SET u.is_deleted = TRUE,"
            + " u.is_active = FALSE"
            + " WHERE u.id = :userId";

        final MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("userId", userId);

        operations.update(sql, parameters);
    }

    public void updateUserPrivileges(final Long userId, final boolean hasPrivileges) {
        final String sql = "UPDATE rentcalculator.users u"
            + " SET u.has_privileges = :hasPrivileges"
            + " WHERE u.id = :userId";

        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("hasPrivileges", hasPrivileges);

        operations.update(sql, parameterSource);
    }

}

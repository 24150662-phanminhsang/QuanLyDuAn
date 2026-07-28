package dao;

import model.AccountStatus;
import model.Role;
import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements BaseDAO<User> {

    private static final String SELECT_ALL = """
            SELECT
                u.user_id,
                u.username,
                u.password_hash,
                u.full_name,
                u.email,
                u.phone,
                u.role_id,
                r.role_name,
                u.status,
                u.created_at,
                u.updated_at
            FROM dbo.Users u
            INNER JOIN dbo.Roles r
                ON u.role_id = r.role_id
            ORDER BY u.user_id DESC
            """;

    private static final String SELECT_BY_ID = """
            SELECT
                u.user_id,
                u.username,
                u.password_hash,
                u.full_name,
                u.email,
                u.phone,
                u.role_id,
                r.role_name,
                u.status,
                u.created_at,
                u.updated_at
            FROM dbo.Users u
            INNER JOIN dbo.Roles r
                ON u.role_id = r.role_id
            WHERE u.user_id = ?
            """;

    private static final String SELECT_BY_USERNAME = """
            SELECT
                u.user_id,
                u.username,
                u.password_hash,
                u.full_name,
                u.email,
                u.phone,
                u.role_id,
                r.role_name,
                u.status,
                u.created_at,
                u.updated_at
            FROM dbo.Users u
            INNER JOIN dbo.Roles r
                ON u.role_id = r.role_id
            WHERE u.username = ?
            """;

    private static final String INSERT = """
            INSERT INTO dbo.Users
            (
                username,
                password_hash,
                full_name,
                email,
                phone,
                role_id,
                status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE dbo.Users
            SET
                full_name = ?,
                email = ?,
                phone = ?,
                role_id = ?,
                status = ?,
                updated_at = SYSDATETIME()
            WHERE user_id = ?
            """;

    private static final String DELETE = """
            DELETE FROM dbo.Users
            WHERE user_id = ?
            """;

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(SELECT_ALL);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapResultSet(resultSet));
            }
        }

        return users;
    }

    @Override
    public Optional<User> findById(int id)
            throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(SELECT_BY_ID)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapResultSet(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    public Optional<User> findByUsername(
            String username
    ) throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                SELECT_BY_USERNAME
                        )
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapResultSet(resultSet)
                    );
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean insert(User user)
            throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setInt(6, user.getRoleId());
            statement.setString(
                    7,
                    user.getStatus().name()
            );

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(User user)
            throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(UPDATE)
        ) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhone());
            statement.setInt(4, user.getRoleId());
            statement.setString(
                    5,
                    user.getStatus().name()
            );
            statement.setInt(6, user.getUserId());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id)
            throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(DELETE)
        ) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    private User mapResultSet(ResultSet resultSet)
            throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getInt("user_id")
        );

        user.setUsername(
                resultSet.getString("username")
        );

        user.setPasswordHash(
                resultSet.getString("password_hash")
        );

        user.setFullName(
                resultSet.getString("full_name")
        );

        user.setEmail(
                resultSet.getString("email")
        );

        user.setPhone(
                resultSet.getString("phone")
        );

        user.setRoleId(
                resultSet.getInt("role_id")
        );

        user.setRole(
                Role.valueOf(
                        resultSet.getString("role_name")
                )
        );

        user.setStatus(
                AccountStatus.valueOf(
                        resultSet.getString("status")
                )
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            user.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp("updated_at");

        if (updatedAt != null) {
            user.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return user;
    }
}
package db.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.api.migration.MigrationChecksumProvider;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

public class V4__replace_old_scopes implements JdbcMigration, MigrationChecksumProvider {

    @Override
    public Integer getChecksum() {
        return 1546262391;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        List<Long> clientIds = clientIds(connection);
        for (Long clientId : clientIds) {
            removeScopeFromClient(clientId, "GET", connection);
            removeScopeFromClient(clientId, "POST", connection);
            removeScopeFromClient(clientId, "PUT", connection);
            removeScopeFromClient(clientId, "PATCH", connection);
            removeScopeFromClient(clientId, "DELETE", connection);

            addScopeToClient(clientId, "ADMIN", connection);
            addScopeToClient(clientId, "ME", connection);
        }

        removeScopeFromClient(toInternalId("auth-server", connection), "ME", connection);
        removeScopeFromClient(toInternalId("addon-administration-client", connection), "ME", connection);
        removeScopeFromClient(toInternalId("addon-self-administration-client", connection), "ME", connection);
    }

    private List<Long> clientIds(Connection connection) throws SQLException {
        List<Long> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select internal_id from osiam_client")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long clientId = resultSet.getLong(1);
                result.add(clientId);
            }
            return result;
        }
    }

    private void removeScopeFromClient(Long clientId, String scope, Connection connection) throws SQLException {
        if(clientId < 0) {
            return;
        }
        try (PreparedStatement statement = connection
                .prepareStatement("delete from osiam_client_scopes where id = ? and scope = ?")) {
            statement.setLong(1, clientId);
            statement.setString(2, scope);
            statement.execute();
        }
    }

    private void addScopeToClient(Long clientId, String scope, Connection connection) throws SQLException {
        if(clientId < 0) {
            return;
        }
        try (PreparedStatement statement = connection
                .prepareStatement("insert into osiam_client_scopes (id, scope) values (?, ?)")) {
            statement.setLong(1, clientId);
            statement.setString(2, scope);
            statement.execute();
        }
    }

    private long toInternalId(String clientId, Connection connection) {
        try (PreparedStatement statement = connection
                .prepareStatement("select internal_id from osiam_client where id = ?")) {
            statement.setString(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return -1;
        } catch (SQLException e) {
            return -1;
        }
    }
}

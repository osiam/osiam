package db.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.flywaydb.core.api.migration.MigrationChecksumProvider;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

public class V4__replace_old_scopes implements JdbcMigration, MigrationChecksumProvider {

    private final Map<String, Long> internalIds = new HashMap<>();
    private Connection connection;

    @Override
    public Integer getChecksum() {
        return 1546262391;
    }

    @Override
    public void migrate(Connection connection) throws Exception {
        this.connection = connection;
        removeScopesFromClient("example-client", "GET", "POST", "PUT", "PATCH", "DELETE");
        addScopesToClient("example-client", "ADMIN", "ME");
        removeScopesFromClient("auth-server", "GET", "POST", "PUT", "PATCH", "DELETE");
        addScopesToClient("auth-server", "ADMIN");
        removeScopesFromClient("addon-self-administration-client", "GET", "POST", "PUT", "PATCH", "DELETE");
        addScopesToClient("addon-self-administration-client", "ADMIN");
        removeScopesFromClient("addon-administration-client", "GET", "POST", "PUT", "PATCH", "DELETE");
        addScopesToClient("addon-administration-client", "ADMIN");
    }

    private void removeScopesFromClient(String clientId, String... scopes) throws SQLException {
        long internalId = toInternalId(clientId);
        if (internalId < 0) {
            return;
        }
        for (String scope : scopes) {
            try (PreparedStatement statement = connection
                    .prepareStatement("delete from osiam_client_scopes where id = ? and scope = ?")) {
                statement.setLong(1, internalId);
                statement.setString(2, scope);
                statement.execute();
            }
        }
    }

    private void addScopesToClient(String clientId, String... scopes) throws SQLException {
        long internalId = toInternalId(clientId);
        if (internalId < 0) {
            return;
        }
        for (String scope : scopes) {
            try (PreparedStatement statement = connection
                    .prepareStatement("insert into osiam_client_scopes (id, scope) values (?, ?)")) {
                statement.setLong(1, internalId);
                statement.setString(2, scope);
                statement.execute();
            }
        }
    }

    private long toInternalId(String clientId) {
        if (!internalIds.containsKey(clientId)) {
            try (PreparedStatement statement = connection
                    .prepareStatement("select internal_id from osiam_client where id = ?")) {
                statement.setString(1, clientId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    internalIds.put(clientId, resultSet.getLong(1));
                } else {
                    internalIds.put(clientId, -1L);
                }
            } catch (SQLException e) {
                internalIds.put(clientId, -1L);
            }
        }
        return internalIds.get(clientId);
    }
}

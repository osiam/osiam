package db.migration;

import java.util.List;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V4__replace_old_scopes implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        List<Long> clientIds = jdbcTemplate.queryForList("select internal_id from osiam_client", Long.class);
        for (Long clientId : clientIds) {
            jdbcTemplate.update("DELETE FROM osiam_client_scopes WHERE id = ? and scope = ?", clientId, "GET");
            jdbcTemplate.update("DELETE FROM osiam_client_scopes WHERE id = ? and scope = ?", clientId, "POST");
            jdbcTemplate.update("DELETE FROM osiam_client_scopes WHERE id = ? and scope = ?", clientId, "PUT");
            jdbcTemplate.update("DELETE FROM osiam_client_scopes WHERE id = ? and scope = ?", clientId, "PATCH");
            jdbcTemplate.update("DELETE FROM osiam_client_scopes WHERE id = ? and scope = ?", clientId, "DELETE");

            jdbcTemplate.update("INSERT INTO osiam_client_scopes (id, scope) VALUES (?, ?)", clientId, "ADMIN");
            jdbcTemplate.update("INSERT INTO osiam_client_scopes (id, scope) VALUES (?, ?)", clientId, "ME");
        }

        updateKnownClient("auth-server", jdbcTemplate);
        updateKnownClient("addon-administration-client", jdbcTemplate);
        updateKnownClient("addon-self-administration-client", jdbcTemplate);
    }

    private void updateKnownClient(String clientId, JdbcTemplate jdbcTemplate) {
        List<Long> ids = jdbcTemplate.queryForList(
                "select internal_id from osiam_client where id = ?",
                new Object[] { clientId },
                Long.class
        );
        for (Long id : ids) {
            jdbcTemplate.update("DELETE FROM osiam_client_scopes WHERE id = ? and scope = ?", id, "ME");
        }
    }
}

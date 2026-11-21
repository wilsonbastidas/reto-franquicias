package co.com.nequi.r2dbc.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresqlConnectionPropertiesTest {

    @Test
    void shouldCreateRecord() {
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
                "localhost", 5432, "db", "public", "user", "pass");

        assertThat(properties.host()).isEqualTo("localhost");
        assertThat(properties.port()).isEqualTo(5432);
        assertThat(properties.database()).isEqualTo("db");
        assertThat(properties.schema()).isEqualTo("public");
        assertThat(properties.username()).isEqualTo("user");
        assertThat(properties.password()).isEqualTo("pass");
    }
}
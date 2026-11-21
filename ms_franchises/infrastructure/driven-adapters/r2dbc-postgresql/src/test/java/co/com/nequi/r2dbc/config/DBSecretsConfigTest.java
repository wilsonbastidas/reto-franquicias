package co.com.nequi.r2dbc.config;

import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DBSecretsConfigTest {

    @Test
    void shouldGetSecretFromAwsSecretsManager() {
        PostgresqlConnectionProperties expected = new PostgresqlConnectionProperties(
                "localhost", 5432, "db", "public", "user", "pass");
        AWSSecretManagerConnectorAsync connector = mock(AWSSecretManagerConnectorAsync.class);
        when(connector.getSecret(eq("secret-name"), eq(PostgresqlConnectionProperties.class)))
                .thenReturn(Mono.just(expected));

        DBSecretsConfig config = new DBSecretsConfig("us-east-1", "http://localhost:4566") {
            @Override
            AWSSecretManagerConnectorAsync createConnector() {
                return connector;
            }
        };

        PostgresqlConnectionProperties result = config.getRdsSecretConfig("secret-name");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldCreateConnector() {
        DBSecretsConfig config = new DBSecretsConfig("us-east-1", "http://localhost:4566");
        assertThat(config.createConnector()).isNotNull();
    }
}
package co.com.nequi.r2dbc.config;

import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.regions.Region;

import java.time.Duration;

@Configuration
public class DBSecretsConfig {

    private static final int SECRET_CACHE_SIZE = 5;
    private static final int SECRET_CACHE_SECONDS = 3600;
    private static final Duration SECRET_TIMEOUT = Duration.ofSeconds(30);

    private final String region;
    private final String endpoint;

    public DBSecretsConfig(@Value("${aws.region}") String region,
                           @Value("${aws.secret.endpoint:}") String endpoint) {
        this.region = region;
        this.endpoint = endpoint;
    }

    @Bean
    public PostgresqlConnectionProperties getRdsSecretConfig(@Value("${aws.rds.secret-name}") String rdsSecret) {
        return Mono.defer(() -> createConnector().getSecret(rdsSecret, PostgresqlConnectionProperties.class))
                .subscribeOn(Schedulers.boundedElastic())
                .block(SECRET_TIMEOUT);
    }

    AWSSecretManagerConnectorAsync createConnector() {
        return new AWSSecretManagerConnectorAsync(getConfig());
    }

    private AWSSecretsManagerConfig getConfig() {
        return AWSSecretsManagerConfig.builder()
                .region(Region.of(region))
                .endpoint(endpoint)
                .cacheSize(SECRET_CACHE_SIZE)
                .cacheSeconds(SECRET_CACHE_SECONDS)
                .build();
    }
}

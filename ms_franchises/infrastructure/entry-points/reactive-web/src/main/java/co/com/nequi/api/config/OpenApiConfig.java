package co.com.nequi.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MS Franchises API",
                version = "1.0",
                description = "API for managing franchises, branches, and products"
        )
)
public class OpenApiConfig {
}
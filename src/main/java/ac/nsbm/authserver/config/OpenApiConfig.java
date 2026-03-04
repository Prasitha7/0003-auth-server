package ac.nsbm.authserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Auth Server API")
                .version("v1")
                .description("OAuth2 Authorization Server and user management APIs"));
    }
}

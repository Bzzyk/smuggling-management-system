package pl.edu.pb.smuggling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smugglingManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smuggling Management System API")
                        .version("1.0.0")
                        .description("REST API for the educational database systems project."));
    }
}

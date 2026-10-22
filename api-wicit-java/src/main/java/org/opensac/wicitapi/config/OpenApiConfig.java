package org.opensac.wicitapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title and description for the generated OpenAPI document and Swagger UI ({@code /swagger-ui.html}).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Describes the API.
     *
     * @return the OpenAPI metadata
     */
    @Bean
    public OpenAPI wicitOpenApi() {
        return new OpenAPI().info(new Info()
                .title("WICit API")
                .description("California WIC data for the WICit website (findwic.com): authorized vendors, "
                        + "authorized products, farmers' markets, participant counts and redemptions. The data comes "
                        + "from the California Department of Public Health on data.chhs.ca.gov.")
                .version("v1")
                .license(new License().name("MIT")));
    }
}

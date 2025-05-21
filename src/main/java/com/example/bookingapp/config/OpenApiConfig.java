package com.example.bookingapp.config;

import com.example.bookingapp.util.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(Constants.SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(Constants.SCHEME_TYPE)
                                .bearerFormat(Constants.BEARER_FORMAT)))
                .addSecurityItem(new SecurityRequirement().addList(Constants.SECURITY_SCHEME_NAME));
    }
}

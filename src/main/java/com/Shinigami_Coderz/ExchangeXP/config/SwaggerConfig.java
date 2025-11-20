package com.Shinigami_Coderz.ExchangeXP.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig(){
        return new OpenAPI()
                .info(
                    new Info().title("ExchangeXP APIs")
                              .description("ExchangeXP API Documentation")
                )
                .servers(Arrays.asList(
                    new Server().url("http://localhost:8080/exchangexp/api/").description("local"),
                    new Server().url("https://k3j36wf9-8080.asse.devtunnels.ms/exchangexp/api/").description("production")
                ))
                .tags(Arrays.asList(
                    new Tag().name("Public APIs"),
                    new Tag().name("Auth APIs"),
                    new Tag().name("User APIs"),
                    new Tag().name("Blog APIs"),
                    new Tag().name("Blog Comment APIs"),
                    new Tag().name("Blog Like APIs"),
                    new Tag().name("Admin APIs")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ));
    }
}

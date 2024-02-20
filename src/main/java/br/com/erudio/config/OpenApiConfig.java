package br.com.erudio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    
    @Bean
    OpenAPI customOpemApi() {
        return new OpenAPI()
                .info(new Info()
                    .title("JUnit Open API")
                    .version("1.0.0")
                    .description("Test for JUnit5 Open API")
                    .termsOfService("https://")
                    .license(
                        new License()
                        .name("MIT")
                        .url("https://")));
    }
}

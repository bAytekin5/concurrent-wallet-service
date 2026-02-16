package com.berkay.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Virtual Wallet API")
                        .version("1.0.0")
                        .description("A Virtual Wallet application developed for educational and testing purposes" +
                                "featuring concurrency scenarios and an optimistic locking mechanism.")
                        .contact(new Contact()
                                .name("Berkay Aytekin")
                                .url("https://github.com/bAytekin5")));
    }
}

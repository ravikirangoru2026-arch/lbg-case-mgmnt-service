package com.lbg.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI caseServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlertDesk — Case Management Service API")
                        .description("""
                            Investigation lifecycle management for Economic Crime Prevention.
                            
                            **Case Lifecycle:**
                            `OPEN → UNDER_INVESTIGATION → PENDING_REVIEW → SAR_FILED / NO_ACTION_TAKEN / CLOSED`
                            
                            Stage skipping returns **422 Unprocessable Entity**.
                            SAR filing is only valid from **PENDING_REVIEW** status.
                            Notes are **immutable** once written.
                            Audit log is **append-only** — written automatically on every state change.
                            """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("ECP Engineering Team")
                                .email("ecp-tech@lbg.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8082")
                        .description("Local Case Management Service"));
    }
}
package de.tomwey2.taskappbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    // Deine bestehende Bean für die allgemeinen API-Infos und Security
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Geändert zu Bearer für JWT
        return new OpenAPI()
                .info(new Info().title("TaskApp API")
                        .version("1.0")
                        .description("API für die TaskApp-Anwendung.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer hateoasLinksCustomizer() {
        return openApi -> {
            final String linkSchemaName = "Link";
            final String linksSchemaName = "Links";

            // 1. Definiere das Schema für ein EINZELNES Link-Objekt, falls es noch nicht existiert.
            // Dies macht den Code unabhängig von der automatischen Generierung.
            if (!openApi.getComponents().getSchemas().containsKey(linkSchemaName)) {
                Schema<?> singleLinkSchema = new Schema<>()
                        .type("object")
                        .description("Represents a single HATEOAS link.")
                        .properties(Map.of(
                                "href", new StringSchema().description("The target URI of the link."),
                                "templated", new Schema<>().type("boolean").description("Indicates if the link is a URI template."),
                                "type", new StringSchema().description("The media type of the target resource."),
                                "deprecation", new StringSchema().description("Indicates that the link is deprecated."),
                                "profile", new StringSchema().description("A URI that hints about the profile of the target resource."),
                                "name", new StringSchema().description("A name for the link."),
                                "hreflang", new StringSchema().description("The language of the target resource.")
                        ));
                openApi.getComponents().addSchemas(linkSchemaName, singleLinkSchema);
            }

            // 2. Erstelle ein neues, korrektes Schema für den "_links"-Container.
            // Dieses Schema verwendet eine Referenz auf unser gerade definiertes "Link"-Schema.
            Schema<?> linksContainerSchema = new Schema<>()
                    .type("object")
                    .description("Container for HATEOAS links. The keys are the link relations (e.g., 'self', 'reportedBy').")
                    .additionalProperties(new Schema<>().$ref("#/components/schemas/" + linkSchemaName));

            // Füge unser neues, korrektes "Links"-Schema zu den globalen Komponenten hinzu.
            openApi.getComponents().addSchemas(linksSchemaName, linksContainerSchema);

            // 3. Gehe durch alle Schemas und ersetze das fehlerhafte "_links"-Schema
            //    durch eine Referenz auf unser neues, korrektes "Links"-Schema.
            if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
                openApi.getComponents().getSchemas().values().forEach(schema -> {
                    if (schema.getProperties() != null && schema.getProperties().containsKey("_links")) {
                        schema.getProperties().put("_links", new Schema<>().$ref("#/components/schemas/" + linksSchemaName));
                    }
                });
            }
        };
    }
}

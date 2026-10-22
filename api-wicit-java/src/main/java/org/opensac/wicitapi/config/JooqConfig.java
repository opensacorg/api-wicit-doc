package org.opensac.wicitapi.config;

import org.jooq.conf.RenderQuotedNames;
import org.springframework.boot.jooq.autoconfigure.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jOOQ settings.
 */
@Configuration
public class JooqConfig {

    /**
     * Renders table and column names without quotes. Every name in the schema is lower case, so PostgreSQL
     * reads them the same either way, and the H2 test database matches the names Liquibase created there.
     *
     * @return the customizer
     */
    @Bean
    public DefaultConfigurationCustomizer unquotedNames() {
        return configuration -> configuration.settings().withRenderQuotedNames(RenderQuotedNames.NEVER);
    }
}

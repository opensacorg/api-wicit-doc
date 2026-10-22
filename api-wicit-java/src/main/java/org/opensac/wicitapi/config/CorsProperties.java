package org.opensac.wicitapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Browser origins allowed to call the API, set under {@code wicit.cors} in application.yaml.
 *
 * @param allowedOriginPatterns origins such as {@code https://findwic.com}; {@code *} matches a subdomain
 */
@ConfigurationProperties("wicit.cors")
public record CorsProperties(List<String> allowedOriginPatterns) {

    /**
     * Defaults to no allowed origins.
     */
    public CorsProperties {
        allowedOriginPatterns = allowedOriginPatterns == null ? List.of() : List.copyOf(allowedOriginPatterns);
    }
}

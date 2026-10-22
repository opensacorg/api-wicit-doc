package org.opensac.wicitapi.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Lets the WICit frontend call the API from another origin: GitHub Pages, findwic.com, or the Vite
 * dev server. The API is read-only, so only safe methods are allowed.
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    /**
     * Creates the configuration.
     *
     * @param corsProperties the allowed origins
     */
    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(corsProperties.allowedOriginPatterns().toArray(String[]::new))
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .exposedHeaders("Content-Disposition")
                .maxAge(3600);
    }
}

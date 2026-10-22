package org.opensac.wicitapi.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * Brands Swagger UI with the WICit favicon and logo. Swagger UI's page links to its own PNG icons and to
 * {@code index.css}; these mappings take precedence over the webjar resources that would serve them. The
 * stylesheet swaps the Swagger logo in the top bar for {@code static/logo.svg}, served at {@code /logo.svg}.
 */
@Hidden
@RestController
public class SwaggerUiBrandingController {

    private static final Resource FAVICON = new ClassPathResource("static/favicon.ico");

    private static final Resource STYLESHEET = new ClassPathResource("swagger-ui/index.css");

    /**
     * Returns the favicon.
     *
     * @return the favicon
     */
    @GetMapping({"/swagger-ui/favicon-32x32.png", "/swagger-ui/favicon-16x16.png"})
    public ResponseEntity<Resource> favicon() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/x-icon"))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
                .body(FAVICON);
    }

    /**
     * Returns Swagger UI's page stylesheet with the WICit branding. Browsers revalidate it on every load, so
     * a change to it shows up without waiting for a cache to expire.
     *
     * @return the stylesheet
     */
    @GetMapping("/swagger-ui/index.css")
    public ResponseEntity<Resource> stylesheet() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/css"))
                .cacheControl(CacheControl.noCache())
                .body(STYLESHEET);
    }
}

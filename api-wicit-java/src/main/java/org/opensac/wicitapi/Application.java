package org.opensac.wicitapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the WICit API.
 */
@SpringBootApplication
public class Application {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments passed to Spring
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

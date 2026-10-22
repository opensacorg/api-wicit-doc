package org.opensac.wicitapi;

import org.opensac.wicitapi.config.JooqConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A jOOQ test slice on the in-memory H2 database, with the schema built by the Liquibase changelog and the
 * application's jOOQ settings. Each test runs in a transaction that is rolled back afterwards.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JooqTest
@ActiveProfiles("test")
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
@Import(JooqConfig.class)
public @interface DatabaseTest {
}

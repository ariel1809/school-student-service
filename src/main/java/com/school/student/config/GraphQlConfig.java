package com.school.student.config;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Guardrails against abusive GraphQL queries. Spring for GraphQL auto-detects
 * {@link Instrumentation} beans and applies them to every execution.
 * <p>
 * Introspection should additionally be disabled in production via
 * {@code spring.graphql.schema.introspection.enabled=false} (left on in dev for GraphiQL).
 */
@Configuration
public class GraphQlConfig {

    @Bean
    public Instrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(10);
    }

    @Bean
    public Instrumentation maxQueryComplexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(200);
    }
}
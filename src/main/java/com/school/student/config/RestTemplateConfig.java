package com.school.student.config;

import com.school.common.security.SecurityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Load-balanced {@link RestTemplate} (Eureka service names) that forwards the gateway auth
 * headers from the inbound request, so downstream services see the same caller identity.
 * Mirrors the config-service setup.
 */
@Configuration
public class RestTemplateConfig {

    private static final List<String> FORWARDED_HEADERS = List.of(
            SecurityHeaders.USER_ID,
            SecurityHeaders.USER_EMAIL,
            SecurityHeaders.USER_NAME,
            SecurityHeaders.USER_ROLES,
            SecurityHeaders.USER_PERMISSIONS,
            SecurityHeaders.REQUEST_ID
    );

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add(new GatewayHeaderForwardingInterceptor());
        return rt;
    }

    private static class GatewayHeaderForwardingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpServletRequest current = currentRequest();
            if (current != null) {
                for (String header : FORWARDED_HEADERS) {
                    String value = current.getHeader(header);
                    if (value != null && !value.isBlank() && !request.getHeaders().containsKey(header)) {
                        request.getHeaders().add(header, value);
                    }
                }
            }
            return execution.execute(request, body);
        }

        private HttpServletRequest currentRequest() {
            var attrs = RequestContextHolder.getRequestAttributes();
            return (attrs instanceof ServletRequestAttributes sra) ? sra.getRequest() : null;
        }
    }
}
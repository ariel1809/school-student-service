package com.school.student.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.school.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * Validates and denormalizes a (country, region?, city?) selection by calling
 * config-service's geo reference module via Eureka service discovery.
 * Auth headers are forwarded by the load-balanced RestTemplate interceptor.
 */
@Slf4j
@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class ConfigGeoClient {

    private static final String RESOLVE_URL = "http://config-service/api/v1/geo/resolve";

    private final RestTemplate restTemplate;

    public GeoResolution resolve(String countryCode, UUID regionId, UUID cityId) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(RESOLVE_URL)
                .queryParam("countryCode", countryCode);
        if (regionId != null) uri.queryParam("regionId", regionId);
        if (cityId != null) uri.queryParam("cityId", cityId);

        try {
            var response = restTemplate.exchange(
                    uri.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponseEnvelope<GeoResolution>>() {});
            ApiResponseEnvelope<GeoResolution> envelope = response.getBody();
            if (envelope == null || envelope.data() == null) {
                throw new BusinessException("CONFIG_GEO_EMPTY_RESPONSE",
                        "config-service returned an empty geo resolution", HttpStatus.BAD_GATEWAY);
            }
            return envelope.data();
        } catch (RestClientResponseException e) {
            // 4xx from config-service means the selection was invalid; surface it as a bad request.
            if (e.getStatusCode().is4xxClientError()) {
                throw new BusinessException("GEO_INVALID",
                        "Invalid country/region/city selection", HttpStatus.BAD_REQUEST);
            }
            log.warn("config-service geo resolve failed: status={} body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException("CONFIG_GEO_UNAVAILABLE",
                    "Could not validate location with config-service", HttpStatus.BAD_GATEWAY);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeoResolution(
            String countryCode,
            String countryName,
            UUID regionId,
            String regionName,
            UUID cityId,
            String cityName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiResponseEnvelope<T>(boolean success, String message, T data) {
    }
}
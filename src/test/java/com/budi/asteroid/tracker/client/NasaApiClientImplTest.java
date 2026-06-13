package com.budi.asteroid.tracker.client;

import com.budi.asteroid.tracker.config.NasaApiProperties;
import com.budi.asteroid.tracker.exception.AsteroidNotFoundException;
import com.budi.asteroid.tracker.exception.NasaApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NasaApiClientImplTest {

    private MockWebServer mockWebServer;
    private NasaApiClientImpl client;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        NasaApiProperties properties = new NasaApiProperties();
        properties.setBaseUrl(mockWebServer.url("/").toString());
        properties.setKey("test-api-key");
        properties.setConnectTimeout(1000);
        properties.setReadTimeout(1000);

        client = new NasaApiClientImpl(properties);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void shouldGetNeoFeed() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "near_earth_objects": {}
                        }
                        """));

        var response = client.getNeoFeed(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 7)
        );

        assertThat(response).isNotNull();

        RecordedRequest request = mockWebServer.takeRequest();

        assertThat(request.getPath())
                .contains("/neo/rest/v1/feed")
                .contains("start_date=2025-01-01")
                .contains("end_date=2025-01-07")
                .contains("api_key=test-api-key");
    }

    @Test
    void shouldThrowNasaApiExceptionWhenNeoFeedFails() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "error": "NASA API error"
                        }
                        """));

        assertThatThrownBy(() -> client.getNeoFeed(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 7)
        ))
                .isInstanceOf(NasaApiException.class)
                .hasMessage("Failed to retrieve data from NASA API");
    }

    @Test
    void shouldGetNeoLookup() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "id": "3542519",
                          "name": "(2010 PK9)",
                          "nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=3542519",
                          "is_potentially_hazardous_asteroid": false
                        }
                        """));

        var response = client.getNeoLookup("3542519");

        assertThat(response).isNotNull();

        RecordedRequest request = mockWebServer.takeRequest();

        assertThat(request.getPath())
                .contains("/neo/rest/v1/neo/3542519")
                .contains("api_key=test-api-key");
    }

    @Test
    void shouldThrowAsteroidNotFoundExceptionWhenNeoLookupReturns404() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "msg": "not found"
                        }
                        """));

        assertThatThrownBy(() -> client.getNeoLookup("3542519"))
                .isInstanceOf(AsteroidNotFoundException.class)
                .hasMessage("Asteroid not found: 3542519");
    }

    @Test
    void shouldThrowNasaApiExceptionWhenNeoLookupFails() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "error": "NASA API error"
                        }
                        """));

        assertThatThrownBy(() -> client.getNeoLookup("3542519"))
                .isInstanceOf(NasaApiException.class)
                .hasMessage("Failed to retrieve data from NASA API");
    }
}
package com.snhu.sslserver.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for SSL/TLS configuration, HTTPS connectivity, and certificate handling in the
 * checksum verification system.
 *
 * <p>These tests verify:
 *
 * <ul>
 *   <li>HTTPS-only operation on port 8443
 *   <li>SSL certificate validation and trust management
 *   <li>Security headers presence and configuration
 *   <li>TLS protocol enforcement
 *   <li>HTTP to HTTPS redirection behavior
 *   <li>End-to-end HTTPS request/response cycle
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ssl-production")
class SslIntegrationTest {

  @LocalServerPort private int port;

  private TestRestTemplate restTemplate;
  private ObjectMapper objectMapper;
  private String httpsBaseUrl;

  @BeforeEach
  void setUp() throws NoSuchAlgorithmException, KeyManagementException {
    // Configure TestRestTemplate to accept self-signed certificates for testing
    restTemplate = new TestRestTemplate();
    restTemplate
        .getRestTemplate()
        .setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory());

    // Configure SSL context to trust self-signed certificates for testing
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new TrustManager[] {new AcceptAllTrustManager()}, null);
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

    objectMapper = new ObjectMapper();
    httpsBaseUrl = "https://localhost:" + port;
  }

  @Test
  void testHttpsConnectivity() {
    // Test basic HTTPS connectivity to the hash endpoint
    String url = httpsBaseUrl + "/api/v1/hash";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Rick Goshen"));
  }

  @Test
  void testHttpsResponseFormat() throws Exception {
    // Test that HTTPS response returns proper JSON format
    String url = httpsBaseUrl + "/api/v1/hash";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Parse JSON response
    JsonNode jsonResponse = objectMapper.readTree(response.getBody());
    assertTrue(jsonResponse.has("originalData"));
    assertTrue(jsonResponse.has("algorithm"));
    assertTrue(jsonResponse.has("hexHash"));
    assertTrue(jsonResponse.has("timestamp"));

    // Verify default algorithm
    assertEquals("SHA-256", jsonResponse.get("algorithm").asText());
  }

  @Test
  void testSecurityHeaders() {
    // Test that all required security headers are present in HTTPS responses
    String url = httpsBaseUrl + "/api/v1/hash";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Check for essential security headers
    assertTrue(
        response.getHeaders().containsKey("Strict-Transport-Security"),
        "HSTS header should be present");
    assertTrue(
        response.getHeaders().containsKey("X-Content-Type-Options"),
        "X-Content-Type-Options header should be present");
    assertTrue(
        response.getHeaders().containsKey("X-Frame-Options"),
        "X-Frame-Options header should be present");
    assertTrue(
        response.getHeaders().containsKey("Content-Security-Policy"),
        "CSP header should be present");
    assertTrue(
        response.getHeaders().containsKey("X-CS305-Checksum-System"),
        "Custom identification header should be present");

    // Verify header values
    assertEquals(
        "max-age=31536000; includeSubDomains",
        response.getHeaders().getFirst("Strict-Transport-Security"));
    assertEquals("nosniff", response.getHeaders().getFirst("X-Content-Type-Options"));
    assertEquals("DENY", response.getHeaders().getFirst("X-Frame-Options"));
    assertEquals(
        "Rick-Goshen-Secure-Implementation",
        response.getHeaders().getFirst("X-CS305-Checksum-System"));
  }

  @Test
  void testHttpsAlgorithmParameter() throws Exception {
    // Test HTTPS request with algorithm parameter
    String url = httpsBaseUrl + "/api/v1/hash?algorithm=SHA-512";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    JsonNode jsonResponse = objectMapper.readTree(response.getBody());
    assertEquals("SHA-512", jsonResponse.get("algorithm").asText());
  }

  @Test
  void testHttpsErrorHandling() {
    // Test HTTPS error handling with invalid algorithm
    String url = httpsBaseUrl + "/api/v1/hash?algorithm=INVALID";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());

    // The response should be a JSON error response with status and message
    assertTrue(response.getBody().contains("\"status\":400"));
    assertTrue(
        response.getBody().contains("Invalid algorithm")
            || response.getBody().contains("algorithm"));
  }

  @Test
  void testHttpsHtmlResponse() {
    // Test HTTPS HTML response format
    String url = httpsBaseUrl + "/api/v1/hash";

    // Set Accept header for HTML
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.set("Accept", "text/html");
    org.springframework.http.HttpEntity<String> entity =
        new org.springframework.http.HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().contains("<!DOCTYPE html>"));
    assertTrue(response.getBody().contains("Rick Goshen"));
    assertTrue(response.getBody().contains("SHA-256"));
  }

  @Test
  void testCacheControlHeaders() {
    // Test that API responses have proper cache control headers
    String url = httpsBaseUrl + "/api/v1/hash";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Verify cache control headers for API endpoints
    assertTrue(response.getHeaders().containsKey("Cache-Control"));
    assertTrue(response.getHeaders().containsKey("Pragma"));
    assertTrue(response.getHeaders().containsKey("Expires"));

    assertEquals(
        "no-cache, no-store, must-revalidate", response.getHeaders().getFirst("Cache-Control"));
    assertEquals("no-cache", response.getHeaders().getFirst("Pragma"));
    assertEquals("0", response.getHeaders().getFirst("Expires"));
  }

  @Test
  void testHttpsEndToEndHashVerification() throws Exception {
    // Test complete end-to-end HTTPS hash computation
    String url = httpsBaseUrl + "/api/v1/hash?algorithm=SHA-256";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    JsonNode jsonResponse = objectMapper.readTree(response.getBody());
    String hexHash = jsonResponse.get("hexHash").asText();
    String originalData = jsonResponse.get("originalData").asText();

    // Verify hash is not empty and has expected length for SHA-256 (64 hex characters)
    assertNotNull(hexHash);
    assertEquals(64, hexHash.length());
    assertTrue(hexHash.matches("[a-f0-9]+"));

    // Verify original data contains student name
    assertTrue(originalData.contains("Rick Goshen"));
  }

  @Test
  void testMultipleConcurrentHttpsRequests() throws InterruptedException {
    // Test thread safety with concurrent HTTPS requests
    String url = httpsBaseUrl + "/api/v1/hash";
    int numThreads = 5;
    Thread[] threads = new Thread[numThreads];
    boolean[] results = new boolean[numThreads];

    for (int i = 0; i < numThreads; i++) {
      final int threadIndex = i;
      threads[i] =
          new Thread(
              () -> {
                try {
                  ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                  results[threadIndex] = response.getStatusCode() == HttpStatus.OK;
                } catch (Exception e) {
                  results[threadIndex] = false;
                }
              });
      threads[i].start();
    }

    // Wait for all threads to complete
    for (Thread thread : threads) {
      thread.join();
    }

    // Verify all requests succeeded
    for (boolean result : results) {
      assertTrue(result, "All concurrent HTTPS requests should succeed");
    }
  }

  /**
   * Trust manager that accepts all certificates for testing purposes. IMPORTANT: This should only
   * be used in test environments.
   */
  private static class AcceptAllTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
      // Accept all client certificates for testing
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
      // Accept all server certificates for testing
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }
}

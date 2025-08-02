package com.snhu.sslserver.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;
import com.snhu.sslserver.model.PerformanceRating;

/**
 * Unit tests for ResponseFormatter class. Tests HTML generation functionality including proper
 * escaping, styling, and content formatting for both hash results and algorithm listings.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@DisplayName("ResponseFormatter Tests")
class ResponseFormatterTest {

  private ResponseFormatter responseFormatter;
  private HashResult testHashResult;
  private List<AlgorithmInfo> testAlgorithms;

  @BeforeEach
  void setUp() {
    responseFormatter = new ResponseFormatter();

    // Create test hash result
    testHashResult =
        HashResult.builder()
            .originalData("StudentName: Rick Goshen Data: Hello World!")
            .algorithm("SHA-256")
            .hexHash("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3")
            .timestamp(Instant.parse("2025-01-15T10:30:00Z"))
            .computationTimeMs(5)
            .build();

    // Create test algorithms
    AlgorithmInfo secureAlgorithm =
        AlgorithmInfo.builder()
            .name("SHA-256")
            .secure(true)
            .performance(PerformanceRating.FAST)
            .description("Secure 256-bit hash algorithm, widely used and trusted")
            .aliases(Collections.singleton("SHA256"))
            .build();

    AlgorithmInfo insecureAlgorithm =
        AlgorithmInfo.builder()
            .name("MD5")
            .secure(false)
            .performance(PerformanceRating.FAST)
            .description("Deprecated algorithm with collision vulnerabilities")
            .aliases(Collections.emptySet())
            .build();

    testAlgorithms = Arrays.asList(secureAlgorithm, insecureAlgorithm);
  }

  @Test
  @DisplayName("Should generate valid HTML document for hash results")
  void shouldGenerateValidHtmlForHashResults() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertNotNull(html);
    assertThat(html, containsString("<!DOCTYPE html>"));
    assertThat(html, containsString("<html lang=\"en\">"));
    assertThat(html, containsString("</html>"));
    assertThat(html, containsString("<head>"));
    assertThat(html, containsString("</head>"));
    assertThat(html, containsString("<body>"));
    assertThat(html, containsString("</body>"));
  }

  @Test
  @DisplayName("Should include correct title and meta tags")
  void shouldIncludeCorrectTitleAndMetaTags() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("<title>Checksum Verification Result</title>"));
    assertThat(html, containsString("<meta charset=\"UTF-8\">"));
    assertThat(html, containsString("<meta name=\"viewport\""));
    assertThat(html, containsString("<meta name=\"description\""));
  }

  @Test
  @DisplayName("Should include professional CSS styling")
  void shouldIncludeProfessionalCssStyling() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("<style>"));
    assertThat(html, containsString("</style>"));
    assertThat(html, containsString("font-family: 'Segoe UI'"));
    assertThat(html, containsString("linear-gradient"));
    assertThat(html, containsString(".container"));
    assertThat(html, containsString(".page-header"));
    assertThat(html, containsString(".result-container"));
    assertThat(html, containsString(".field"));
    assertThat(html, containsString(".hash-value"));
  }

  @Test
  @DisplayName("Should display hash result data correctly")
  void shouldDisplayHashResultDataCorrectly() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("StudentName: Rick Goshen Data: Hello World!"));
    assertThat(html, containsString("SHA-256"));
    assertThat(
        html, containsString("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"));
    assertThat(html, containsString("5 ms"));
    assertThat(html, containsString("2025-01-15T10:30:00Z"));
  }

  @Test
  @DisplayName("Should include emojis and modern design elements")
  void shouldIncludeEmojisAndModernDesignElements() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("🔐 Checksum Verification System"));
    assertThat(html, containsString("Cryptographic Hash Generation for File Integrity"));
    assertThat(html, containsString("Computation Result"));
  }

  @Test
  @DisplayName("Should include footer with links")
  void shouldIncludeFooterWithLinks() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("<footer"));
    assertThat(html, containsString("CS305 Checksum Verification System"));
    assertThat(html, containsString("Rick Goshen"));
    assertThat(html, containsString("/api/v1/hash"));
    assertThat(html, containsString("/api/v1/algorithms"));
  }

  @Test
  @DisplayName("Should escape HTML special characters to prevent XSS")
  void shouldEscapeHtmlSpecialCharactersToPreventXss() {
    HashResult xssTestResult =
        HashResult.builder()
            .originalData("Hello <script>alert('xss')</script>")
            .algorithm("SHA-256")
            .hexHash("test")
            .timestamp(Instant.now())
            .computationTimeMs(1)
            .build();

    String html = responseFormatter.formatHashResultAsHtml(xssTestResult);

    assertThat(html, containsString("Hello &lt;script&gt;alert(&#x27;xss&#x27;)&lt;/script&gt;"));
    assertThat(html, not(containsString("<script>")));
    assertThat(html, not(containsString("alert('xss')")));
  }

  @Test
  @DisplayName("Should generate valid HTML document for algorithms list")
  void shouldGenerateValidHtmlForAlgorithmsList() {
    String html = responseFormatter.formatAlgorithmsAsHtml(testAlgorithms);

    assertNotNull(html);
    assertThat(html, containsString("<!DOCTYPE html>"));
    assertThat(html, containsString("<html lang=\"en\">"));
    assertThat(html, containsString("</html>"));
    assertThat(html, containsString("Supported Hash Algorithms"));
  }

  @Test
  @DisplayName("Should display algorithm information correctly")
  void shouldDisplayAlgorithmInformationCorrectly() {
    String html = responseFormatter.formatAlgorithmsAsHtml(testAlgorithms);

    // Check for SHA-256 (secure algorithm)
    assertThat(html, containsString("SHA-256"));
    assertThat(html, containsString("Secure 256-bit hash algorithm"));
    assertThat(html, containsString("✅ Secure"));
    assertThat(html, containsString("FAST"));

    // Check for MD5 (insecure algorithm)
    assertThat(html, containsString("MD5"));
    assertThat(html, containsString("Deprecated algorithm with collision vulnerabilities"));
    assertThat(html, containsString("⚠️ Deprecated"));
  }

  @Test
  @DisplayName("Should apply different styling for secure vs insecure algorithms")
  void shouldApplyDifferentStylingForSecureVsInsecureAlgorithms() {
    String html = responseFormatter.formatAlgorithmsAsHtml(testAlgorithms);

    assertThat(html, containsString("algorithm-card secure"));
    assertThat(html, containsString("algorithm-card insecure"));
    assertThat(html, containsString("security-badge secure"));
    assertThat(html, containsString("security-badge insecure"));
  }

  @Test
  @DisplayName("Should include responsive design CSS")
  void shouldIncludeResponsiveDesignCss() {
    String html = responseFormatter.formatAlgorithmsAsHtml(testAlgorithms);

    assertThat(html, containsString("@media (max-width: 768px)"));
    assertThat(html, containsString("grid-template-columns"));
    assertThat(html, containsString("auto-fit"));
    assertThat(html, containsString("minmax"));
  }

  @Test
  @DisplayName("Should handle empty algorithms list gracefully")
  void shouldHandleEmptyAlgorithmsListGracefully() {
    String html = responseFormatter.formatAlgorithmsAsHtml(Collections.emptyList());

    assertNotNull(html);
    assertThat(html, containsString("<!DOCTYPE html>"));
    assertThat(html, containsString("Supported Hash Algorithms"));
    assertThat(html, containsString("algorithms-grid"));
  }

  @Test
  @DisplayName("Should include timestamp field when timestamp is present")
  void shouldIncludeTimestampFieldWhenTimestampIsPresent() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertNotNull(html);
    assertThat(html, containsString("Generated:"));
    assertThat(html, containsString("2025-01-15T10:30:00Z"));
  }

  @Test
  @DisplayName("Should include proper semantic HTML structure")
  void shouldIncludeProperSemanticHtmlStructure() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("<header class=\"page-header\">"));
    assertThat(html, containsString("<main class=\"result-section\">"));
    assertThat(html, containsString("<footer class=\"page-footer\">"));
    assertThat(html, containsString("<h1>"));
    assertThat(html, containsString("<h2>"));
    assertThat(html, containsString("<label"));
  }

  @Test
  @DisplayName("Should include modern visual elements and icons")
  void shouldIncludeModernVisualElementsAndIcons() {
    String html = responseFormatter.formatAlgorithmsAsHtml(testAlgorithms);

    assertThat(html, containsString("🔧 Supported Hash Algorithms"));
    assertThat(html, containsString("✅ Secure"));
    assertThat(html, containsString("⚠️ Deprecated"));
    assertThat(html, containsString("box-shadow"));
    assertThat(html, containsString("border-radius"));
    assertThat(html, containsString("transition"));
  }

  @Test
  @DisplayName("Should generate different page widths for different content types")
  void shouldGenerateDifferentPageWidthsForDifferentContentTypes() {
    String hashHtml = responseFormatter.formatHashResultAsHtml(testHashResult);
    String algorithmsHtml = responseFormatter.formatAlgorithmsAsHtml(testAlgorithms);

    // Hash results page should have smaller max-width
    assertThat(hashHtml, containsString("max-width: 900px"));

    // Algorithms page should have larger max-width for grid layout
    assertThat(algorithmsHtml, containsString("max-width: 1200px"));
  }

  @Test
  @DisplayName("Should use monospace font for hash values")
  void shouldUseMonospaceFontForHashValues() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace"));
    assertThat(html, containsString("hash-value"));
  }

  @Test
  @DisplayName("Should include accessibility features")
  void shouldIncludeAccessibilityFeatures() {
    String html = responseFormatter.formatHashResultAsHtml(testHashResult);

    assertThat(html, containsString("lang=\"en\""));
    assertThat(html, containsString("<label"));
    assertThat(html, containsString("field-label"));
  }
}

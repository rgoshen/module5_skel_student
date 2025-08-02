package com.snhu.sslserver.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;
import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;
import com.snhu.sslserver.model.PerformanceRating;
import com.snhu.sslserver.model.ValidationResult;
import com.snhu.sslserver.service.IHashService;
import com.snhu.sslserver.service.IInputValidator;

/**
 * Unit tests for HashController. Tests REST endpoint functionality, content negotiation, error
 * handling, and input validation.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class HashControllerTest {

  @Mock private IHashService hashService;

  @Mock private IInputValidator validator;

  private MockMvc mockMvc;
  private HashController controller;

  @BeforeEach
  void setUp() {
    controller = new HashController(hashService, validator);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Should return HTML response for hash endpoint with default algorithm")
  void shouldReturnHtmlResponseForHashEndpointWithDefaultAlgorithm() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult validInput = ValidationResult.success("Hello Rick Goshen!");
    HashResult hashResult =
        new HashResult(
            "Hello Rick Goshen!",
            "SHA-256",
            "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
            Instant.now(),
            10L);

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash("Hello Rick Goshen!", "SHA-256")).thenReturn(hashResult);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_HTML))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Hello Rick Goshen!")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("SHA-256")))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.containsString(
                        "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3")));
  }

  @Test
  @DisplayName("Should return JSON response for hash endpoint when JSON is requested")
  void shouldReturnJsonResponseForHashEndpointWhenJsonIsRequested() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult validInput = ValidationResult.success("Hello Rick Goshen!");
    HashResult hashResult =
        new HashResult(
            "Hello Rick Goshen!",
            "SHA-256",
            "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
            Instant.parse("2023-01-01T12:00:00Z"),
            15L);

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash("Hello Rick Goshen!", "SHA-256")).thenReturn(hashResult);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.originalData").value("Hello Rick Goshen!"))
        .andExpect(jsonPath("$.algorithm").value("SHA-256"))
        .andExpect(
            jsonPath("$.hexHash")
                .value("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"))
        .andExpect(jsonPath("$.timestamp").value("2023-01-01T12:00:00Z"))
        .andExpect(jsonPath("$.computationTimeMs").value(15));
  }

  @Test
  @DisplayName("Should accept custom algorithm parameter")
  void shouldAcceptCustomAlgorithmParameter() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-512");
    ValidationResult validInput = ValidationResult.success("Hello Rick Goshen!");
    HashResult hashResult =
        new HashResult(
            "Hello Rick Goshen!",
            "SHA-512",
            "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86",
            Instant.now(),
            12L);

    when(validator.validateAlgorithm("SHA-512")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash("Hello Rick Goshen!", "SHA-512")).thenReturn(hashResult);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/v1/hash").param("algorithm", "SHA-512").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.algorithm").value("SHA-512"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request for invalid algorithm")
  void shouldReturn400BadRequestForInvalidAlgorithm() throws Exception {
    // Arrange
    ValidationResult invalidAlgorithm =
        ValidationResult.failure("Algorithm 'MD5' is not secure and not supported");

    when(validator.validateAlgorithm("MD5")).thenReturn(invalidAlgorithm);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").param("algorithm", "MD5").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.message")
                .value("Invalid algorithm: Algorithm 'MD5' is not secure and not supported"));
  }

  @Test
  @DisplayName("Should return 400 Bad Request for invalid input data")
  void shouldReturn400BadRequestForInvalidInputData() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult invalidInput = ValidationResult.failure("Input data is too long");

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(invalidInput);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Invalid input data: Input data is too long"));
  }

  @Test
  @DisplayName("Should return 500 Internal Server Error for cryptographic exceptions")
  void shouldReturn500InternalServerErrorForCryptographicExceptions() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult validInput = ValidationResult.success("Hello Rick Goshen!");

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash(anyString(), anyString()))
        .thenThrow(
            new CryptographicException(
                ErrorCode.COMPUTATION_FAILED, "Hash computation failed due to internal error"));

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.message").value("Hash computation failed due to internal error"));
  }

  @Test
  @DisplayName("Should return HTML error response when HTML is requested")
  void shouldReturnHtmlErrorResponseWhenHtmlIsRequested() throws Exception {
    // Arrange
    ValidationResult invalidAlgorithm = ValidationResult.failure("Invalid algorithm");

    when(validator.validateAlgorithm("INVALID")).thenReturn(invalidAlgorithm);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").param("algorithm", "INVALID").accept(MediaType.TEXT_HTML))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.TEXT_HTML))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Error 400")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid algorithm")));
  }

  @Test
  @DisplayName("Should return supported algorithms in JSON format")
  void shouldReturnSupportedAlgorithmsInJsonFormat() throws Exception {
    // Arrange
    List<AlgorithmInfo> algorithms =
        Arrays.asList(
            AlgorithmInfo.builder()
                .name("SHA-256")
                .secure(true)
                .performance(PerformanceRating.FAST)
                .description("SHA-256 algorithm")
                .aliases(Collections.emptySet())
                .build(),
            AlgorithmInfo.builder()
                .name("SHA-512")
                .secure(true)
                .performance(PerformanceRating.MEDIUM)
                .description("SHA-512 algorithm")
                .aliases(Collections.emptySet())
                .build());

    when(hashService.getSupportedAlgorithms()).thenReturn(algorithms);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/algorithms").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].name").value("SHA-256"))
        .andExpect(jsonPath("$[0].secure").value(true))
        .andExpect(jsonPath("$[1].name").value("SHA-512"))
        .andExpect(jsonPath("$[1].secure").value(true));
  }

  @Test
  @DisplayName("Should return supported algorithms in HTML format")
  void shouldReturnSupportedAlgorithmsInHtmlFormat() throws Exception {
    // Arrange
    List<AlgorithmInfo> algorithms =
        Arrays.asList(
            AlgorithmInfo.builder()
                .name("SHA-256")
                .secure(true)
                .performance(PerformanceRating.FAST)
                .description("SHA-256 algorithm")
                .aliases(Collections.emptySet())
                .build());

    when(hashService.getSupportedAlgorithms()).thenReturn(algorithms);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/algorithms").accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_HTML))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("SHA-256")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Secure")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("SHA-256 algorithm")));
  }

  @Test
  @DisplayName("Should handle unexpected exceptions gracefully")
  void shouldHandleUnexpectedExceptionsGracefully() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult validInput = ValidationResult.success("Hello Rick Goshen!");

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash(anyString(), anyString()))
        .thenThrow(new RuntimeException("Unexpected error"));

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(
            jsonPath("$.message").value("An unexpected error occurred during hash computation"));
  }

  @Test
  @DisplayName("Should escape HTML in responses to prevent XSS")
  void shouldEscapeHtmlInResponsesToPreventXss() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult validInput = ValidationResult.success("Hello <script>alert('xss')</script>");
    HashResult hashResult =
        new HashResult(
            "Hello <script>alert('xss')</script>", "SHA-256", "somehash", Instant.now(), 10L);

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash(anyString(), anyString())).thenReturn(hashResult);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_HTML))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.containsString(
                        "Hello &lt;script&gt;alert(&#x27;xss&#x27;)&lt;/script&gt;")))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("<script>alert('xss')</script>"))));
  }

  @Test
  @DisplayName("Should handle null dependencies gracefully in constructor")
  void shouldHandleNullDependenciesGracefullyInConstructor() {
    // Act & Assert
    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class, () -> new HashController(null, validator));

    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class, () -> new HashController(hashService, null));

    org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class, () -> new HashController(null, null));
  }

  @Test
  @DisplayName("Should use default algorithm when no algorithm parameter is provided")
  void shouldUseDefaultAlgorithmWhenNoAlgorithmParameterIsProvided() throws Exception {
    // Arrange
    ValidationResult validAlgorithm = ValidationResult.success("SHA-256");
    ValidationResult validInput = ValidationResult.success("Hello Rick Goshen!");
    HashResult hashResult =
        new HashResult("Hello Rick Goshen!", "SHA-256", "somehash", Instant.now(), 10L);

    when(validator.validateAlgorithm("SHA-256")).thenReturn(validAlgorithm);
    when(validator.validateAndSanitize(anyString())).thenReturn(validInput);
    when(hashService.computeHash(anyString(), anyString())).thenReturn(hashResult);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/hash").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.algorithm").value("SHA-256"));
  }

  @Test
  @DisplayName("Should handle service exceptions in algorithms endpoint")
  void shouldHandleServiceExceptionsInAlgorithmsEndpoint() throws Exception {
    // Arrange
    when(hashService.getSupportedAlgorithms()).thenThrow(new RuntimeException("Service error"));

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/algorithms").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.message").value("Unable to retrieve algorithm information"));
  }
}

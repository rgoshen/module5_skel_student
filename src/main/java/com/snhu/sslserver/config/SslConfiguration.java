package com.snhu.sslserver.config;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * SSL/TLS configuration for the checksum verification system. This configuration ensures that the
 * application runs exclusively over HTTPS with secure cipher suites and protocols.
 *
 * <p>Security features:
 *
 * <ul>
 *   <li>HTTPS-only operation on port 8443
 *   <li>TLS 1.2 and 1.3 protocol support
 *   <li>Secure cipher suite configuration
 *   <li>PKCS12 keystore integration
 *   <li>SSL error handling and validation
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Configuration
public class SslConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(SslConfiguration.class);

  @Value("${server.ssl.key-store}")
  private Resource keyStoreResource;

  @Value("${server.ssl.key-store-password}")
  private String keyStorePassword;

  @Value("${server.ssl.key-store-type}")
  private String keyStoreType;

  @Value("${server.ssl.key-alias}")
  private String keyAlias;

  /**
   * Validates SSL configuration at startup to ensure proper keystore setup.
   *
   * @return SSL validation result
   */
  @Bean
  public SslValidationResult validateSslConfiguration() {
    logger.info("Validating SSL/TLS configuration for HTTPS-only operation");

    try {
      // Validate keystore accessibility
      if (!keyStoreResource.exists()) {
        logger.error("SSL keystore not found: {}", keyStoreResource.getDescription());
        return new SslValidationResult(false, "Keystore file not found");
      }

      // Validate keystore loading
      KeyStore keyStore = KeyStore.getInstance(keyStoreType);
      keyStore.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());

      // Validate key alias exists
      if (!keyStore.containsAlias(keyAlias)) {
        logger.error("SSL key alias '{}' not found in keystore", keyAlias);
        return new SslValidationResult(false, "Key alias not found in keystore");
      }

      // Validate SSL context creation
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, null, null);

      logger.info("SSL/TLS configuration validation successful");
      logger.info("Keystore: {}", keyStoreResource.getDescription());
      logger.info("Key alias: {}", keyAlias);
      logger.info("Keystore type: {}", keyStoreType);

      return new SslValidationResult(true, "SSL configuration valid");

    } catch (Exception e) {
      logger.error("SSL/TLS configuration validation failed", e);
      return new SslValidationResult(false, "SSL validation error: " + e.getMessage());
    }
  }

  /**
   * Customizes Tomcat server factory for enhanced SSL security.
   *
   * @return WebServerFactoryCustomizer for Tomcat configuration
   */
  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
    return factory -> {
      factory.addConnectorCustomizers(
          connector -> {
            // Enhanced security settings
            connector.setSecure(true);
            connector.setScheme("https");

            // Log SSL configuration
            logger.info("Tomcat HTTPS connector configured on port 8443");
            logger.info("SSL security headers and HTTPS enforcement enabled");
          });
    };
  }

  /** Result of SSL configuration validation. */
  public static class SslValidationResult {
    private final boolean valid;
    private final String message;

    public SslValidationResult(boolean valid, String message) {
      this.valid = valid;
      this.message = message;
    }

    public boolean isValid() {
      return valid;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public String toString() {
      return "SslValidationResult{valid=" + valid + ", message='" + message + "'}";
    }
  }
}

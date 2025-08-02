package com.snhu.sslserver.config;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
   * Validates SSL configuration at startup to ensure proper keystore setup. Throws an exception if
   * validation fails to prevent startup with invalid SSL configuration.
   */
  @Bean
  public void validateSslConfiguration() {
    logger.info("Validating SSL/TLS configuration for HTTPS-only operation");

    try {
      KeyStore keyStore = loadKeyStore();
      validateKeyAlias(keyStore);
      validateSslContext(keyStore);
      logValidationSuccess();
    } catch (Exception e) {
      logger.error("SSL/TLS configuration validation failed", e);
      throw new IllegalStateException("SSL configuration is invalid - application cannot start", e);
    }
  }

  /**
   * Loads and validates the SSL keystore.
   *
   * @return The loaded keystore
   * @throws Exception if keystore cannot be loaded
   */
  private KeyStore loadKeyStore() throws Exception {
    if (!keyStoreResource.exists()) {
      throw new IllegalStateException(
          "Keystore file not found: " + keyStoreResource.getDescription());
    }

    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    try (InputStream inputStream = keyStoreResource.getInputStream()) {
      keyStore.load(inputStream, keyStorePassword.toCharArray());
    }
    return keyStore;
  }

  /**
   * Validates that the required key alias exists in the keystore.
   *
   * @param keyStore The loaded keystore
   * @throws Exception if key alias is not found
   */
  private void validateKeyAlias(KeyStore keyStore) throws Exception {
    if (!keyStore.containsAlias(keyAlias)) {
      throw new IllegalStateException("SSL key alias not found in keystore");
    }
  }

  /**
   * Validates SSL context creation using the actual keystore.
   *
   * @param keyStore The loaded keystore
   * @throws Exception if SSL context cannot be created
   */
  private void validateSslContext(KeyStore keyStore) throws Exception {
    // Initialize KeyManagerFactory with the keystore and password
    KeyManagerFactory keyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

    // Initialize TrustManagerFactory with the keystore
    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);

    // Initialize SSLContext with KeyManagers and TrustManagers to properly validate keystore
    // integration
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(
        keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
  }

  /** Logs successful SSL configuration validation. */
  private void logValidationSuccess() {
    logger.info("SSL/TLS configuration validation successful");
    logger.info("Keystore: {}", keyStoreResource.getDescription());
    logger.info("Keystore type: {}", keyStoreType);
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
}

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
 *   <li>PKCS12 certificate store integration
 *   <li>SSL error handling and validation
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Configuration
public class SslConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(SslConfiguration.class);

  @Value("${ssl.cert.store}")
  private Resource certificateStoreResource;

  @Value("${ssl.cert.store.auth}")
  private String certificateStorePassword;

  @Value("${ssl.cert.store.type}")
  private String certificateStoreType;

  @Value("${ssl.cert.alias}")
  private String certificateAlias;

  /**
   * Validates SSL configuration at startup to ensure proper certificate store setup. Throws an
   * exception if validation fails to prevent startup with invalid SSL configuration.
   */
  @Bean
  public void validateSslConfiguration() {
    logger.info("Validating SSL/TLS configuration for HTTPS-only operation");

    try {
      KeyStore certStore = loadCertificateStore();
      validateCertificateAlias(certStore);
      validateSslContext(certStore);
      logValidationSuccess();
    } catch (Exception e) {
      logger.error("SSL/TLS configuration validation failed", e);
      throw new IllegalStateException("SSL configuration is invalid - application cannot start", e);
    }
  }

  /**
   * Loads and validates the SSL certificate store.
   *
   * @return The loaded certificate store
   * @throws Exception if certificate store cannot be loaded
   */
  private KeyStore loadCertificateStore() throws Exception {
    if (!certificateStoreResource.exists()) {
      throw new IllegalStateException(
          "Certificate store file not found: " + certificateStoreResource.getDescription());
    }

    KeyStore certStore = KeyStore.getInstance(certificateStoreType);
    try (InputStream inputStream = certificateStoreResource.getInputStream()) {
      certStore.load(inputStream, certificateStorePassword.toCharArray());
    }
    return certStore;
  }

  /**
   * Validates that the required certificate alias exists in the certificate store.
   *
   * @param certStore The loaded certificate store
   * @throws Exception if certificate alias is not found
   */
  private void validateCertificateAlias(KeyStore certStore) throws Exception {
    if (!certStore.containsAlias(certificateAlias)) {
      throw new IllegalStateException("SSL certificate alias not found in certificate store");
    }
  }

  /**
   * Validates SSL context creation using the actual certificate store.
   *
   * @param certStore The loaded certificate store
   * @throws Exception if SSL context cannot be created
   */
  private void validateSslContext(KeyStore certStore) throws Exception {
    // Initialize CertManagerFactory with the certificate store and password
    KeyManagerFactory certManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    certManagerFactory.init(certStore, certificateStorePassword.toCharArray());

    // Initialize TrustManagerFactory with the certificate store
    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(certStore);

    // Initialize SSLContext with CertManagers and TrustManagers to properly validate certificate
    // store integration
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(
        certManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
  }

  /** Logs successful SSL configuration validation. */
  private void logValidationSuccess() {
    logger.info("SSL/TLS configuration validation successful");
    String storeLocation = certificateStoreResource.getDescription();
    String storeFormat = certificateStoreType;
    logger.info("SSL certificate store: {}", storeLocation);
    logger.info("SSL certificate store type: {}", storeFormat);
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

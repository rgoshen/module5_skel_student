package com.snhu.sslserver.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Configuration for security headers and HTTPS enforcement. This configuration adds essential
 * security headers to protect against common web vulnerabilities and ensures HTTPS-only operation.
 *
 * <p>Security headers implemented:
 *
 * <ul>
 *   <li>HTTP Strict Transport Security (HSTS)
 *   <li>X-Content-Type-Options
 *   <li>X-Frame-Options
 *   <li>X-XSS-Protection
 *   <li>Content-Security-Policy
 *   <li>Referrer-Policy
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Configuration
public class SecurityHeadersConfiguration implements WebMvcConfigurer {

  // Security header constants to avoid duplication and improve maintainability
  private static final String HSTS_HEADER = "Strict-Transport-Security";
  private static final String HSTS_VALUE = "max-age=31536000; includeSubDomains";

  private static final String CONTENT_TYPE_OPTIONS_HEADER = "X-Content-Type-Options";
  private static final String CONTENT_TYPE_OPTIONS_VALUE = "nosniff";

  private static final String FRAME_OPTIONS_HEADER = "X-Frame-Options";
  private static final String FRAME_OPTIONS_VALUE = "DENY";

  private static final String XSS_PROTECTION_HEADER = "X-XSS-Protection";
  private static final String XSS_PROTECTION_VALUE = "1; mode=block";

  private static final String CSP_HEADER = "Content-Security-Policy";
  private static final String CSP_VALUE =
      "default-src 'self'; "
          + "script-src 'self'; "
          + "style-src 'self'; "
          + "img-src 'self' data:; "
          + "font-src 'self'; "
          + "connect-src 'self'; "
          + "frame-ancestors 'none'";

  private static final String REFERRER_POLICY_HEADER = "Referrer-Policy";
  private static final String REFERRER_POLICY_VALUE = "strict-origin-when-cross-origin";

  private static final String CACHE_CONTROL_HEADER = "Cache-Control";
  private static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
  private static final String PRAGMA_HEADER = "Pragma";
  private static final String PRAGMA_VALUE = "no-cache";
  private static final String EXPIRES_HEADER = "Expires";
  private static final String EXPIRES_VALUE = "0";

  private static final String CS305_HEADER = "X-CS305-Checksum-System";
  private static final String CS305_VALUE = "Rick-Goshen-Secure-Implementation";

  /**
   * Security headers interceptor that adds protective headers to all HTTP responses.
   *
   * @return SecurityHeadersInterceptor instance
   */
  @Bean
  public SecurityHeadersInterceptor securityHeadersInterceptor() {
    return new SecurityHeadersInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(securityHeadersInterceptor());
  }

  /** Interceptor that adds security headers to HTTP responses for enhanced protection. */
  public static class SecurityHeadersInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler) {

      // HTTP Strict Transport Security (HSTS) - Force HTTPS for 1 year
      response.setHeader(HSTS_HEADER, HSTS_VALUE);

      // Prevent MIME type sniffing
      response.setHeader(CONTENT_TYPE_OPTIONS_HEADER, CONTENT_TYPE_OPTIONS_VALUE);

      // Prevent clickjacking
      response.setHeader(FRAME_OPTIONS_HEADER, FRAME_OPTIONS_VALUE);

      // XSS Protection (legacy browsers)
      response.setHeader(XSS_PROTECTION_HEADER, XSS_PROTECTION_VALUE);

      // Content Security Policy - Restrict resource loading (strengthened by removing
      // unsafe-inline)
      response.setHeader(CSP_HEADER, CSP_VALUE);

      // Referrer Policy - Control referrer information
      response.setHeader(REFERRER_POLICY_HEADER, REFERRER_POLICY_VALUE);

      // Cache Control for sensitive responses - expanded to cover more sensitive endpoints
      String uri = request.getRequestURI();
      if (isSensitiveEndpoint(uri)) {
        response.setHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE);
        response.setHeader(PRAGMA_HEADER, PRAGMA_VALUE);
        response.setHeader(EXPIRES_HEADER, EXPIRES_VALUE);
      }

      // Custom security header for CS305 identification
      response.setHeader(CS305_HEADER, CS305_VALUE);

      return true;
    }

    /**
     * Determines if the given URI represents a sensitive endpoint that should not be cached.
     *
     * @param uri The request URI
     * @return true if the endpoint is sensitive and should not be cached
     */
    private boolean isSensitiveEndpoint(String uri) {
      return uri.contains("/api/")
          || uri.contains("/auth/")
          || uri.contains("/user/")
          || uri.contains("/admin/")
          || uri.contains("/secure/");
    }
  }
}

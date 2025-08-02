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
      response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

      // Prevent MIME type sniffing
      response.setHeader("X-Content-Type-Options", "nosniff");

      // Prevent clickjacking
      response.setHeader("X-Frame-Options", "DENY");

      // XSS Protection (legacy browsers)
      response.setHeader("X-XSS-Protection", "1; mode=block");

      // Content Security Policy - Restrict resource loading
      response.setHeader(
          "Content-Security-Policy",
          "default-src 'self'; "
              + "script-src 'self' 'unsafe-inline'; "
              + "style-src 'self' 'unsafe-inline'; "
              + "img-src 'self' data:; "
              + "font-src 'self'; "
              + "connect-src 'self'; "
              + "frame-ancestors 'none'");

      // Referrer Policy - Control referrer information
      response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

      // Cache Control for sensitive responses
      if (request.getRequestURI().contains("/api/")) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
      }

      // Custom security header for CS305 identification
      response.setHeader("X-CS305-Checksum-System", "Rick-Goshen-Secure-Implementation");

      return true;
    }
  }
}

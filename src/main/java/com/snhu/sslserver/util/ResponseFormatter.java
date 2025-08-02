package com.snhu.sslserver.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;

/**
 * Utility class for formatting and generating professional HTML responses for the Checksum
 * Verification System. This class provides clean, consistent HTML templates with modern styling and
 * proper escaping for security.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Professional CSS styling with modern design patterns
 *   <li>Proper HTML escaping to prevent XSS attacks
 *   <li>Responsive design for various screen sizes
 *   <li>Accessible markup with semantic HTML
 *   <li>Consistent branding and visual hierarchy
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class ResponseFormatter {

  // HTML constants to reduce duplication
  private static final String FIELD_DIV_START = "                <div class=\"field\">\n";
  private static final String FIELD_DIV_END = "                </div>\n";
  private static final String FIELD_VALUE_DIV_END = "</div>\n";
  private static final String CONTAINER_DIV_END = "            </div>\n";
  private static final String FIELD_LABEL_TEMPLATE =
      "                    <label class=\"field-label\">%s:</label>\n";
  private static final String FIELD_VALUE_START = "                    <div class=\"field-value";

  // Common HTML page structure
  private static final String SYSTEM_TITLE = "🔐 Checksum Verification System";
  private static final String SYSTEM_SUBTITLE = "Cryptographic Hash Generation for File Integrity";

  /**
   * Generates a professionally formatted HTML response for hash computation results.
   *
   * @param result The hash computation result containing original data, algorithm, and hash value
   * @return Complete HTML document with embedded CSS styling
   */
  public String formatHashResultAsHtml(HashResult result) {
    StringBuilder html = new StringBuilder();

    appendHtmlHeader(html, "Checksum Verification Result");
    appendHashResultStyles(html);
    appendHtmlBodyStart(html);

    appendPageHeader(html, SYSTEM_TITLE, SYSTEM_SUBTITLE);

    html.append("        <main class=\"result-section\">\n");
    html.append("            <h2>Computation Result</h2>\n");
    html.append("            <div class=\"result-container\">\n");

    // Generate hash result fields
    appendHashResultField(html, "Original Data", result.getOriginalData(), "");
    appendHashResultField(html, "Algorithm", result.getAlgorithm(), " algorithm-badge");
    appendHashResultField(html, "Hash Value", result.getHexHash(), " hash-value");
    appendHashResultField(
        html, "Computation Time", result.getComputationTimeMs() + " ms", " performance");

    if (result.getTimestamp() != null) {
      appendHashResultField(html, "Generated", result.getTimestamp().toString(), " timestamp");
    }

    html.append(CONTAINER_DIV_END);
    html.append("        </main>\n\n");

    appendPageFooter(html);

    appendHtmlBodyEnd(html);
    return html.toString();
  }

  /**
   * Generates a professionally formatted HTML response for supported algorithms list.
   *
   * @param algorithms List of supported algorithm information
   * @return Complete HTML document with algorithm listing and descriptions
   */
  public String formatAlgorithmsAsHtml(List<AlgorithmInfo> algorithms) {
    StringBuilder html = new StringBuilder();

    appendHtmlHeader(html, "Supported Hash Algorithms");
    appendAlgorithmListStyles(html);
    appendHtmlBodyStart(html);

    appendPageHeader(
        html, "🔧 Supported Hash Algorithms", "Cryptographically Secure Algorithms Available");

    html.append("        <main class=\"algorithms-section\">\n");
    html.append("            <div class=\"algorithms-grid\">\n");

    for (AlgorithmInfo algorithm : algorithms) {
      appendAlgorithmCard(html, algorithm);
    }

    html.append(CONTAINER_DIV_END);
    html.append("        </main>\n\n");

    appendPageFooter(html);

    appendHtmlBodyEnd(html);
    return html.toString();
  }

  /**
   * Appends a common page header with title and subtitle.
   *
   * @param html StringBuilder to append content to
   * @param title Main page title
   * @param subtitle Page subtitle
   */
  private void appendPageHeader(StringBuilder html, String title, String subtitle) {
    html.append("    <div class=\"container\">\n");
    html.append("        <header class=\"page-header\">\n");
    html.append("            <h1>").append(title).append("</h1>\n");
    html.append("            <p class=\"subtitle\">").append(subtitle).append("</p>\n");
    html.append("        </header>\n\n");
  }

  /**
   * Appends a common page footer.
   *
   * @param html StringBuilder to append content to
   */
  private void appendPageFooter(StringBuilder html) {
    appendFooter(html);
    html.append("    </div>\n");
  }

  /**
   * Appends a hash result field with label and value.
   *
   * @param html StringBuilder to append content to
   * @param label Field label
   * @param value Field value
   * @param cssClass Additional CSS class for the field value
   */
  private void appendHashResultField(
      StringBuilder html, String label, String value, String cssClass) {
    html.append(FIELD_DIV_START);
    html.append(String.format(FIELD_LABEL_TEMPLATE, label));
    html.append(FIELD_VALUE_START).append(cssClass).append("\">");
    html.append(escapeHtml(value));
    html.append(FIELD_VALUE_DIV_END);
    html.append(FIELD_DIV_END);
  }

  /**
   * Appends an algorithm card to the algorithms grid.
   *
   * @param html StringBuilder to append content to
   * @param algorithm Algorithm information
   */
  private void appendAlgorithmCard(StringBuilder html, AlgorithmInfo algorithm) {
    String securityClass = algorithm.isSecure() ? "secure" : "insecure";
    String securityBadge = algorithm.isSecure() ? "✅ Secure" : "⚠️ Deprecated";

    html.append("                <div class=\"algorithm-card ")
        .append(securityClass)
        .append("\">\n");
    html.append("                    <div class=\"algorithm-header\">\n");
    html.append("                        <h3 class=\"algorithm-name\">")
        .append(escapeHtml(algorithm.getName()))
        .append("</h3>\n");
    html.append("                        <span class=\"security-badge ")
        .append(securityClass)
        .append("\">")
        .append(securityBadge)
        .append("</span>\n");
    html.append("                    </div>\n");

    if (algorithm.getDescription() != null && !algorithm.getDescription().isEmpty()) {
      html.append("                    <p class=\"algorithm-description\">")
          .append(escapeHtml(algorithm.getDescription()))
          .append("</p>\n");
    }

    html.append("                    <div class=\"algorithm-details\">\n");
    if (algorithm.getPerformance() != null) {
      html.append("                        <div class=\"performance-info\">\n");
      html.append("                            <span class=\"detail-label\">Performance:</span>\n");
      html.append("                            <span class=\"performance-rating\">")
          .append(escapeHtml(algorithm.getPerformance().toString()))
          .append("</span>\n");
      html.append("                        </div>\n");
    }
    html.append("                    </div>\n");
    html.append("                </div>\n");
  }

  /**
   * Appends the HTML document header with DOCTYPE, meta tags, and title.
   *
   * @param html StringBuilder to append content to
   * @param title Page title for the document
   */
  private void appendHtmlHeader(StringBuilder html, String title) {
    html.append("<!DOCTYPE html>\n");
    html.append("<html lang=\"en\">\n");
    html.append("<head>\n");
    html.append("    <meta charset=\"UTF-8\">\n");
    html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
    html.append(
        "    <meta name=\"description\" content=\"CS305 Checksum Verification System - Secure cryptographic hash generation\">\n");
    html.append("    <title>");
    html.append(escapeHtml(title));
    html.append("</title>\n");
  }

  /**
   * Appends the CSS styles for hash result pages.
   *
   * @param html StringBuilder to append styles to
   */
  private void appendHashResultStyles(StringBuilder html) {
    html.append("    <style>\n");
    html.append("        * {\n");
    html.append("            margin: 0;\n");
    html.append("            padding: 0;\n");
    html.append("            box-sizing: border-box;\n");
    html.append("        }\n\n");

    html.append("        body {\n");
    html.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
    html.append("            line-height: 1.6;\n");
    html.append("            color: #333;\n");
    html.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n");
    html.append("            min-height: 100vh;\n");
    html.append("            padding: 20px;\n");
    html.append("        }\n\n");

    html.append("        .container {\n");
    html.append("            max-width: 900px;\n");
    html.append("            margin: 0 auto;\n");
    html.append("            background: white;\n");
    html.append("            border-radius: 12px;\n");
    html.append("            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);\n");
    html.append("            overflow: hidden;\n");
    html.append("        }\n\n");

    html.append("        .page-header {\n");
    html.append("            background: linear-gradient(135deg, #2c3e50, #34495e);\n");
    html.append("            color: white;\n");
    html.append("            padding: 2rem;\n");
    html.append("            text-align: center;\n");
    html.append("        }\n\n");

    html.append("        .page-header h1 {\n");
    html.append("            font-size: 2.5rem;\n");
    html.append("            margin-bottom: 0.5rem;\n");
    html.append("            font-weight: 300;\n");
    html.append("        }\n\n");

    html.append("        .subtitle {\n");
    html.append("            font-size: 1.1rem;\n");
    html.append("            opacity: 0.9;\n");
    html.append("            font-weight: 300;\n");
    html.append("        }\n\n");

    html.append("        .result-section {\n");
    html.append("            padding: 2rem;\n");
    html.append("        }\n\n");

    html.append("        .result-section h2 {\n");
    html.append("            color: #2c3e50;\n");
    html.append("            margin-bottom: 1.5rem;\n");
    html.append("            font-size: 1.8rem;\n");
    html.append("            font-weight: 400;\n");
    html.append("        }\n\n");

    html.append("        .result-container {\n");
    html.append("            background: #f8fafc;\n");
    html.append("            border: 1px solid #e2e8f0;\n");
    html.append("            border-radius: 8px;\n");
    html.append("            padding: 1.5rem;\n");
    html.append("        }\n\n");

    html.append("        .field {\n");
    html.append("            margin-bottom: 1.5rem;\n");
    html.append("            border-bottom: 1px solid #e2e8f0;\n");
    html.append("            padding-bottom: 1rem;\n");
    html.append("        }\n\n");

    html.append("        .field:last-child {\n");
    html.append("            border-bottom: none;\n");
    html.append("            margin-bottom: 0;\n");
    html.append("        }\n\n");

    html.append("        .field-label {\n");
    html.append("            display: block;\n");
    html.append("            font-weight: 600;\n");
    html.append("            color: #4a5568;\n");
    html.append("            margin-bottom: 0.5rem;\n");
    html.append("            font-size: 0.9rem;\n");
    html.append("            text-transform: uppercase;\n");
    html.append("            letter-spacing: 0.5px;\n");
    html.append("        }\n\n");

    html.append("        .field-value {\n");
    html.append("            font-size: 1.1rem;\n");
    html.append("            color: #2d3748;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-badge {\n");
    html.append("            background: #48bb78;\n");
    html.append("            color: white;\n");
    html.append("            padding: 0.3rem 0.8rem;\n");
    html.append("            border-radius: 20px;\n");
    html.append("            font-weight: 600;\n");
    html.append("            display: inline-block;\n");
    html.append("            font-size: 0.9rem;\n");
    html.append("        }\n\n");

    html.append("        .hash-value {\n");
    html.append("            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;\n");
    html.append("            background: #1a202c;\n");
    html.append("            color: #68d391;\n");
    html.append("            padding: 1rem;\n");
    html.append("            border-radius: 6px;\n");
    html.append("            word-break: break-all;\n");
    html.append("            font-size: 0.9rem;\n");
    html.append("            border: 1px solid #2d3748;\n");
    html.append("        }\n\n");

    html.append("        .performance {\n");
    html.append("            color: #805ad5;\n");
    html.append("            font-weight: 600;\n");
    html.append("        }\n\n");

    html.append("        .timestamp {\n");
    html.append("            color: #718096;\n");
    html.append("            font-size: 0.95rem;\n");
    html.append("        }\n\n");

    appendFooterStyles(html);
    html.append("    </style>\n");
  }

  /**
   * Appends the CSS styles for algorithm listing pages.
   *
   * @param html StringBuilder to append styles to
   */
  private void appendAlgorithmListStyles(StringBuilder html) {
    html.append("    <style>\n");
    html.append("        * {\n");
    html.append("            margin: 0;\n");
    html.append("            padding: 0;\n");
    html.append("            box-sizing: border-box;\n");
    html.append("        }\n\n");

    html.append("        body {\n");
    html.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
    html.append("            line-height: 1.6;\n");
    html.append("            color: #333;\n");
    html.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n");
    html.append("            min-height: 100vh;\n");
    html.append("            padding: 20px;\n");
    html.append("        }\n\n");

    html.append("        .container {\n");
    html.append("            max-width: 1200px;\n");
    html.append("            margin: 0 auto;\n");
    html.append("            background: white;\n");
    html.append("            border-radius: 12px;\n");
    html.append("            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);\n");
    html.append("            overflow: hidden;\n");
    html.append("        }\n\n");

    html.append("        .page-header {\n");
    html.append("            background: linear-gradient(135deg, #2c3e50, #34495e);\n");
    html.append("            color: white;\n");
    html.append("            padding: 2rem;\n");
    html.append("            text-align: center;\n");
    html.append("        }\n\n");

    html.append("        .page-header h1 {\n");
    html.append("            font-size: 2.5rem;\n");
    html.append("            margin-bottom: 0.5rem;\n");
    html.append("            font-weight: 300;\n");
    html.append("        }\n\n");

    html.append("        .subtitle {\n");
    html.append("            font-size: 1.1rem;\n");
    html.append("            opacity: 0.9;\n");
    html.append("            font-weight: 300;\n");
    html.append("        }\n\n");

    html.append("        .algorithms-section {\n");
    html.append("            padding: 2rem;\n");
    html.append("        }\n\n");

    html.append("        .algorithms-grid {\n");
    html.append("            display: grid;\n");
    html.append("            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));\n");
    html.append("            gap: 1.5rem;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-card {\n");
    html.append("            background: white;\n");
    html.append("            border: 1px solid #e2e8f0;\n");
    html.append("            border-radius: 8px;\n");
    html.append("            padding: 1.5rem;\n");
    html.append("            transition: all 0.3s ease;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-card:hover {\n");
    html.append("            transform: translateY(-2px);\n");
    html.append("            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);\n");
    html.append("        }\n\n");

    html.append("        .algorithm-card.secure {\n");
    html.append("            border-left: 4px solid #48bb78;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-card.insecure {\n");
    html.append("            border-left: 4px solid #f56565;\n");
    html.append("            background: #fed7d7;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-header {\n");
    html.append("            display: flex;\n");
    html.append("            justify-content: space-between;\n");
    html.append("            align-items: center;\n");
    html.append("            margin-bottom: 1rem;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-name {\n");
    html.append("            font-size: 1.3rem;\n");
    html.append("            font-weight: 600;\n");
    html.append("            color: #2d3748;\n");
    html.append("        }\n\n");

    html.append("        .security-badge {\n");
    html.append("            padding: 0.25rem 0.75rem;\n");
    html.append("            border-radius: 20px;\n");
    html.append("            font-size: 0.8rem;\n");
    html.append("            font-weight: 600;\n");
    html.append("        }\n\n");

    html.append("        .security-badge.secure {\n");
    html.append("            background: #48bb78;\n");
    html.append("            color: white;\n");
    html.append("        }\n\n");

    html.append("        .security-badge.insecure {\n");
    html.append("            background: #f56565;\n");
    html.append("            color: white;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-description {\n");
    html.append("            color: #4a5568;\n");
    html.append("            margin-bottom: 1rem;\n");
    html.append("            line-height: 1.6;\n");
    html.append("        }\n\n");

    html.append("        .algorithm-details {\n");
    html.append("            border-top: 1px solid #e2e8f0;\n");
    html.append("            padding-top: 1rem;\n");
    html.append("        }\n\n");

    html.append("        .performance-info {\n");
    html.append("            display: flex;\n");
    html.append("            justify-content: space-between;\n");
    html.append("        }\n\n");

    html.append("        .detail-label {\n");
    html.append("            font-weight: 600;\n");
    html.append("            color: #4a5568;\n");
    html.append("        }\n\n");

    html.append("        .performance-rating {\n");
    html.append("            color: #805ad5;\n");
    html.append("            font-weight: 600;\n");
    html.append("        }\n\n");

    appendFooterStyles(html);
    html.append("    </style>\n");
  }

  /**
   * Appends common footer styles used across all templates.
   *
   * @param html StringBuilder to append styles to
   */
  private void appendFooterStyles(StringBuilder html) {
    html.append("        .page-footer {\n");
    html.append("            background: #f7fafc;\n");
    html.append("            border-top: 1px solid #e2e8f0;\n");
    html.append("            padding: 1.5rem 2rem;\n");
    html.append("            text-align: center;\n");
    html.append("            color: #718096;\n");
    html.append("            font-size: 0.9rem;\n");
    html.append("        }\n\n");

    html.append("        .footer-links {\n");
    html.append("            margin-top: 0.5rem;\n");
    html.append("        }\n\n");

    html.append("        .footer-links a {\n");
    html.append("            color: #4299e1;\n");
    html.append("            text-decoration: none;\n");
    html.append("            margin: 0 0.5rem;\n");
    html.append("        }\n\n");

    html.append("        .footer-links a:hover {\n");
    html.append("            text-decoration: underline;\n");
    html.append("        }\n\n");

    html.append("        @media (max-width: 768px) {\n");
    html.append("            .container {\n");
    html.append("                margin: 0;\n");
    html.append("                border-radius: 0;\n");
    html.append("            }\n");
    html.append("            .page-header h1 {\n");
    html.append("                font-size: 2rem;\n");
    html.append("            }\n");
    html.append("            .algorithms-grid {\n");
    html.append("                grid-template-columns: 1fr;\n");
    html.append("            }\n");
    html.append("        }\n");
  }

  /**
   * Appends the HTML body opening tag.
   *
   * @param html StringBuilder to append content to
   */
  private void appendHtmlBodyStart(StringBuilder html) {
    html.append("</head>\n");
    html.append("<body>\n");
  }

  /**
   * Appends the common footer section used across all pages.
   *
   * @param html StringBuilder to append content to
   */
  private void appendFooter(StringBuilder html) {
    html.append("        <footer class=\"page-footer\">\n");
    html.append(
        "            <p>&copy; 2025 CS305 Checksum Verification System | Rick Goshen</p>\n");
    html.append("            <div class=\"footer-links\">\n");
    html.append("                <a href=\"/api/v1/hash\">Generate Hash</a>\n");
    html.append("                <a href=\"/api/v1/algorithms\">View Algorithms</a>\n");
    html.append("            </div>\n");
    html.append("        </footer>\n");
  }

  /**
   * Appends the HTML body closing tag and document end.
   *
   * @param html StringBuilder to append content to
   */
  private void appendHtmlBodyEnd(StringBuilder html) {
    html.append("</body>\n");
    html.append("</html>");
  }

  /**
   * Escapes HTML special characters to prevent XSS attacks. Uses custom escaping to maintain
   * compatibility with existing tests while providing comprehensive protection.
   *
   * @param text Text to escape
   * @return HTML-escaped text
   */
  private String escapeHtml(String text) {
    if (text == null) {
      return "";
    }
    return text.replace("&", "&amp;") // Must be first to avoid double-escaping
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("`", "&#x60;"); // Backtick
  }
}

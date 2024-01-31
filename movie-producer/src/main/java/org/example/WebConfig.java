package org.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for web-related configurations, including Cross-Origin Resource Sharing (CORS) settings.
 *
 * This class implements the WebMvcConfigurer interface to customize Spring MVC behavior.
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * The external host (origin) allowed for CORS. Replace this with the actual origin of your React app.
     */
    final String externalHost = "http://localhost:3000";

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings for the application.
     *
     * @param registry The CORS registry to configure.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(externalHost)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}

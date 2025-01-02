


@Configuration
public class GatewayConfig {

    @Bean
    public CorsFilter corsFilter() {
        // Create a CORS configuration
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");  // Allow frontend's URL
        config.addAllowedMethod("*");  // Allow all HTTP methods
        config.addAllowedHeader("*");  // Allow all headers
        config.setAllowCredentials(true);  // Allow credentials (cookies, etc.)

        // Register the CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply CORS globally for all routes

        return new CorsFilter(source);  // Spring CorsFilter takes UrlBasedCorsConfigurationSource as constructor
    }
}
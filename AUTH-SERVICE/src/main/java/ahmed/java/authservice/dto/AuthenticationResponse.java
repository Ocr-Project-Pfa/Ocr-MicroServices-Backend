package ahmed.java.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class AuthenticationResponse {
    private String token;
    private boolean requiresMfa;

    // Default constructor
    public AuthenticationResponse() {}

    // Constructor with all fields
    public AuthenticationResponse(String token, boolean requiresMfa) {
        this.token = token;
        this.requiresMfa = requiresMfa;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRequiresMfa() {
        return requiresMfa;
    }

    public void setRequiresMfa(boolean requiresMfa) {
        this.requiresMfa = requiresMfa;
    }

    // Static method to create builder
    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    // Builder class
    public static class AuthenticationResponseBuilder {
        private String token;
        private boolean requiresMfa;

        AuthenticationResponseBuilder() {}

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponseBuilder requiresMfa(boolean requiresMfa) {
            this.requiresMfa = requiresMfa;
            return this;
        }

        public AuthenticationResponse build() {
            return new AuthenticationResponse(token, requiresMfa);
        }
    }
}
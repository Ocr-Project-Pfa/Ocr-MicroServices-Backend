package ahmed.java.authservice.dto;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String username;
    private String otp;
}

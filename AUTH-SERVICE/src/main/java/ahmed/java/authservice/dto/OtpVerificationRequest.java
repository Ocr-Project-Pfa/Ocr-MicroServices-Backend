package ahmed.java.authservice.dto;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String username;
    private String otp;

    public String getOtp() {
        return otp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

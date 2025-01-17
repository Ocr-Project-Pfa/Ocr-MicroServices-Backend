package ahmed.java.authservice.Service;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;  // 5 minutes
    private final Map<String, OtpData> otpCache = Maps.newHashMap();

    private static class OtpData {
        private String otp;
        private long expiryTime;

        // Default constructor
        public OtpData() {}

        // Constructor with parameters
        public OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        // Getters and setters
        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public long getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(long expiryTime) {
            this.expiryTime = expiryTime;
        }
    }

    public String generateOTP(String username) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpCache.put(username, new OtpData(otp, System.currentTimeMillis() + OTP_VALID_DURATION));
        return otp;
    }

    public boolean validateOTP(String username, String otp) {
        OtpData otpData = otpCache.get(username);
        if (otpData == null) {
            return false;
        }

        if (System.currentTimeMillis() > otpData.getExpiryTime()) {
            otpCache.remove(username);
            return false;
        }

        boolean isValid = otpData.getOtp().equals(otp);
        if (isValid) {
            otpCache.remove(username);
        }
        return isValid;
    }
}

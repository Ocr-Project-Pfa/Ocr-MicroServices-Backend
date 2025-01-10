package ahmed.java.authservice.Service;

import ahmed.java.authservice.dto.AuthenticationRequest;
import ahmed.java.authservice.dto.AuthenticationResponse;
import ahmed.java.authservice.dto.RegisterRequest;
import ahmed.java.authservice.exceptions.InvalidOtpException;
import ahmed.java.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ahmed.java.authservice.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  JwtService jwtService;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) {
        ahmed.java.authservice.model.User user = ahmed.java.authservice.model.User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        if (user.isMfaEnabled()) {
            String otp = otpService.generateOTP(user.getUsername());
            emailService.sendOtpEmail(user.getEmail(), otp);
            return AuthenticationResponse.builder()
                    .token(null)
                    .requiresMfa(true)
                    .build();
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .requiresMfa(false)
                .build();
    }

    public AuthenticationResponse verifyOtp(String username, String otp) {
        if (!otpService.validateOTP(username, otp)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        var user = userRepository.findByUsername(username).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .requiresMfa(false)
                .build();
    }


}

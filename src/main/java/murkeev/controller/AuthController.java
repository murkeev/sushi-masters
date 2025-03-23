package murkeev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import murkeev.dto.LoginRequest;
import murkeev.dto.RegistrationRequest;
import murkeev.dto.AuthResponse;
import murkeev.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        String token = authService.registration(request);
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.authenticateAndGenerateToken(request.username(), request.passphrase());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

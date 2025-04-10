package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication (register & login).")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data",
                    content = @Content(schema = @Schema(implementation = IllegalArgumentException.class))),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        log.info("Registration request received for user: {}", request.getName());
        String token = authService.registration(request);
        log.info("User successfully registered: {}", request.getName());
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }

    @Operation(summary = "Login an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.phone());
        String token = authService.authenticateAndGenerateToken(request.phone(), request.password());
        log.info("User successfully authenticated: {}", request.phone());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

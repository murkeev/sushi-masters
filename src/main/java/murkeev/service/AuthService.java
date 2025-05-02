package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.dto.RegistrationRequest;
import murkeev.security.JwtTokenUtil;
import murkeev.security.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public String authenticateAndGenerateToken(String phone, String password) {
        userService.checkUser(phone);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(phone, password));
        UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
        return jwtTokenUtil.generateToken(userDetails);
    }

    public String registration(RegistrationRequest request) {
        userService.addUser(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getPhone());
        return jwtTokenUtil.generateToken(userDetails);
    }
}

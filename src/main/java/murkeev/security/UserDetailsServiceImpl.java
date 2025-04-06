package murkeev.security;

import lombok.AllArgsConstructor;
import murkeev.model.User;
import murkeev.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userService.checkUser(phone);
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                Set.of(user.getRole()));
    }
}

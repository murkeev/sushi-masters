package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}

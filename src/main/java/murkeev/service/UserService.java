package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.exception.EntityNotFoundException;
import murkeev.model.User;
import murkeev.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User checkUser(String login) {
        User user;
        user = userRepository.findByUsername(login).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with login %s not found!", login)));
        if (user == null) {
            throw new EntityNotFoundException("User is null.");
        }
        return user;
    }
}

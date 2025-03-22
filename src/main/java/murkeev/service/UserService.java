package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.dto.RegistrationRequestDto;
import murkeev.exception.EntityNotFoundException;
import murkeev.model.User;
import murkeev.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public User checkUser(String login) {
        User user;
        user = userRepository.findByUsername(login).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with username %s not found!", login)));
        if (user == null) {
            throw new EntityNotFoundException("User is null.");
        }
        return user;
    }

    @Transactional
    public void addUser(RegistrationRequestDto requestDto) {
        User user = modelMapper.map(requestDto, User.class);
        user.setPassphrase(passwordEncoder.encode(requestDto.passphrase()));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new EntityNotFoundException("Failed in saving user");
        }
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("No authenticated user found");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(USER_NOT_FOUND));

//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
//                () -> new EntityNotFoundException(USER_NOT_FOUND));
    }
}

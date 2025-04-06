package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.dto.RegistrationRequest;
import murkeev.exception.EntityAlreadyExistsException;
import murkeev.exception.EntityManipulationException;
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
    public User checkUser(String phone) {
        User user;
        user = userRepository.findByPhone(phone).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with phone %s not found!", phone)));
        if (user == null) {
            throw new EntityNotFoundException("User is null.");
        }
        return user;
    }

    @Transactional
    public void addUser(RegistrationRequest requestDto) {
        if (userRepository.findByPhone(requestDto.getPhone()).isPresent()) {
            throw new EntityAlreadyExistsException("User with this phone already exists");
        }

        requestDto.setName(normalizeName(requestDto.getName()));
        User user = modelMapper.map(requestDto, User.class);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new EntityManipulationException("Failed in saving user");
        }
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No authenticated user found");
        }
        String phone = authentication.getName();
        return userRepository.findByPhone(phone).orElseThrow(
                () -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    public static String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        name = name.trim().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}

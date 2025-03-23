package murkeev.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
public record RegistrationRequest(
        @Pattern(regexp = "^[a-zA-Z0-9]{4,}$",
                message = "Username must be at least 4 characters long and contain only letters and digits")
        String username,
        @Pattern(regexp = "^[a-z]+(\\s+[a-z]+){2,7}$",
                message = "The phrase must contain only lowercase letters and be between 3 and 8 words long.")
        String passphrase,
        String phone
) {
}

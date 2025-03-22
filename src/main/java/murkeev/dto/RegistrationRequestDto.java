package murkeev.dto;

import lombok.*;

@Builder
public record RegistrationRequestDto(
        String username,
        String passphrase,
        String phone
) {
}

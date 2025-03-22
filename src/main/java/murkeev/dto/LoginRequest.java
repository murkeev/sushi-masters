package murkeev.dto;

import lombok.Builder;

@Builder
public record LoginRequest(
        String username,
        String passphrase
) {
}

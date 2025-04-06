package murkeev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @Schema(type = "string", example = "Іван")
    @Pattern(regexp = "^[a-zA-ZА-ЯҐЄІЇа-яґєії']{2,}$",
            message = "Name must be at least 2 characters long and contain only letters")
    private String name;

    @Schema(type = "string", example = "secret123!")
    @Pattern(regexp = "^(?!.*\\s).{5,}$",
            message = "The password must be at least 5 characters long and cannot contain spaces.")
    private String password;

    @Schema(type = "string", example = "+380501234567")
    @Pattern(regexp = "^\\+380\\d{9}$",
            message = "Phone number must be in format +380XXXXXXXXX")
    private String phone;
}

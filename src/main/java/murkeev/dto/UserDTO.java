package murkeev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import murkeev.model.User;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String phone;
    private String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole().name();
    }
}


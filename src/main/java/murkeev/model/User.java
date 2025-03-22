package murkeev.model;

import jakarta.persistence.*;
import jdk.jfr.Timespan;
import lombok.Data;
import murkeev.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(name = "passphrase", nullable = false)
    private String passphrase;

    @Column(nullable = false)
    private String phone;

    @Column(name = "created_at")
    @Timespan
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_USER;
}

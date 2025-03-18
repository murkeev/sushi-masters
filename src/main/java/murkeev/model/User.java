package murkeev.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
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

    @Column(name = "passphrase_hash", nullable = false)
    private String passphraseHash;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
}

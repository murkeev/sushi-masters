package murkeev.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import murkeev.enums.SushiCategory;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Entity
@Table(name = "sushi")
@NoArgsConstructor
public class Sushi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private String ingredients;
    private String image;
    private String weight;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SushiCategory category;
}

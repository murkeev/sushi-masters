package murkeev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import murkeev.enums.SushiCategory;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SushiRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Price cannot be null")
    private BigDecimal price;
    @NotBlank(message = "Ingredients cannot be empty")
    private String ingredients;
    private String image;
    @NotNull(message = "Weight cannot be null")
    private String weight;
    @NotNull(message = "Category cannot be null")
    private SushiCategory category;
}
package murkeev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import murkeev.enums.SushiCategory;
import murkeev.model.CartItem;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemDTO {
    private Long sushiId;
    private int quantity;
    private BigDecimal price;
    private String image;
    private String name;
    private String weight;

    public CartItemDTO(CartItem cartItem) {
        if (cartItem.getSushi() == null) {
            throw new IllegalArgumentException("Sushi in CartItem is null");
        }
        this.sushiId = cartItem.getSushi().getId();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getPrice();
        this.image = cartItem.getSushi().getImage();
        this.name = cartItem.getSushi().getName();
        this.weight = cartItem.getSushi().getWeight();
    }
}


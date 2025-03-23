package murkeev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import murkeev.model.CartItem;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemDTO {
    private Long sushiId;
    private int quantity;
    private BigDecimal price;

    public CartItemDTO(CartItem cartItem) {
        this.sushiId = cartItem.getSushi().getId();
        this.quantity = cartItem.getQuantity();
        this.price = cartItem.getPrice();
    }
}


package murkeev.dto;

import lombok.Data;
import murkeev.model.Cart;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class CartDTO {
    private UUID id;
    private List<CartItemDTO> items;

    public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.items = cart.getItems().stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
    }
}


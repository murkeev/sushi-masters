package murkeev.dto;

import lombok.Data;
import murkeev.model.OrderItem;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long sushiId;
    private int quantity;
    private BigDecimal price;

    public OrderItemDTO(OrderItem item) {
        this.sushiId = item.getSushi().getId();
        this.quantity = item.getQuantity();
        this.price = item.getPrice();
    }
}

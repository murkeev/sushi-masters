package murkeev.dto;

import lombok.Data;
import murkeev.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.*;

@Data
public class OrderDTO {
    private UUID id;
    private UserDTO user;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.user = new UserDTO(order.getUser());
        this.customerName = order.getCustomerName();
        this.customerPhone = order.getCustomerPhone();
        this.deliveryAddress = order.getDeliveryAddress();
        this.totalAmount = order.getTotalAmount();
        this.items = order.getItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
    }
}
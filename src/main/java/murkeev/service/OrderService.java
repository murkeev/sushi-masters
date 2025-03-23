package murkeev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import murkeev.model.*;
import murkeev.repository.OrderItemRepository;
import murkeev.repository.OrderRepository;
import murkeev.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(UUID cartId, String customerName, String customerPhone, String deliveryAddress) {
        Cart cart = cartService.getCartById(cartId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }
        User user = cart.getUser();

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setDeliveryAddress(deliveryAddress);
        order.setTotalAmount(cartService.getCartTotal(cartId));

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setSushi(cartItem.getSushi());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItems.add(orderItemRepository.save(orderItem));
        }

        savedOrder.setItems(orderItems);
        cartService.clearCart(cartId);

        return savedOrder;
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    public List<Order> getOrdersByPhone(String phone) {
        return orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone);
    }
}
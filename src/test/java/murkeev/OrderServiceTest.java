package murkeev;

import murkeev.model.Order;
import murkeev.repository.OrderItemRepository;
import murkeev.repository.OrderRepository;
import murkeev.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderItemRepository orderItemRepository;

    @InjectMocks
    OrderService orderService;

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Order result = orderService.getOrderById(orderId);
        assertEquals(order, result);
    }

    @Test
    void getOrdersByPhone_ShouldReturnList() {
        String phone = "123456";
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone)).thenReturn(orders);
        List<Order> result = orderService.getOrdersByPhone(phone);
        assertEquals(2, result.size());
    }
}

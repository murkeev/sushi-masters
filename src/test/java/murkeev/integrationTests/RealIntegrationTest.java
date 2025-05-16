package murkeev.integrationTests;

import murkeev.model.Cart;
import murkeev.model.CartItem;
import murkeev.model.Order;
import murkeev.repository.CartItemRepository;
import murkeev.repository.CartRepository;
import murkeev.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RealIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void createOrder_WithRealDb_Performance() {
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setPrice(BigDecimal.valueOf(15));
        item.setQuantity(3);
        cartItemRepository.save(item);

        cart.getItems().add(item);
        cartRepository.save(cart);

        UUID cartId = cart.getId();

        long start = System.nanoTime();

        Order order = orderService.createOrder(
                cartId,
                "Real Customer",
                "987654321",
                "Real Address"
        );

        long duration = System.nanoTime() - start;
        System.out.println("Real DB test duration (ns): " + duration);

        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(45), order.getTotalAmount());
        assertFalse(order.getItems().isEmpty());
    }
}

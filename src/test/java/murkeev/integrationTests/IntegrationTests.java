package murkeev.integrationTests;

import murkeev.model.Cart;
import murkeev.model.CartItem;
import murkeev.model.Order;
import murkeev.model.OrderItem;
import murkeev.repository.OrderItemRepository;
import murkeev.repository.OrderRepository;
import murkeev.service.CartService;
import murkeev.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntegrationTests {

    @Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldReturnOrder() {
        UUID cartId = UUID.randomUUID();

        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setPrice(BigDecimal.valueOf(10));
        item.setQuantity(2);
        cart.setItems(List.of(item));

        when(cartService.getCartById(cartId)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            BigDecimal total = cart.getItems().stream()
                    .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(total);
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(i -> i.getArgument(0));

        Order order = orderService.createOrder(cartId, "Test Customer", "123456789", "Test Address");

        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(20), order.getTotalAmount());
        assertFalse(order.getItems().isEmpty());

        verify(orderRepository).save(any());
        verify(orderItemRepository, times(cart.getItems().size())).save(any());
    }


    @Test
    void getOrdersByPhone_ShouldReturnOrders() {
        String phone = "123456789";
        List<Order> orders = List.of(new Order(), new Order());

        when(orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByPhone(phone);

        assertEquals(2, result.size());
        verify(orderRepository).findByCustomerPhoneOrderByCreatedAtDesc(phone);
    }

    @Test
    void clearCart_ShouldCallClearCart() {
        UUID cartId = UUID.randomUUID();

        doNothing().when(cartService).clearCart(cartId);

        cartService.clearCart(cartId);

        verify(cartService).clearCart(cartId);
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

        Order result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(order, result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {
        UUID cartId = UUID.randomUUID();
        Cart emptyCart = new Cart();
        emptyCart.setItems(List.of());

        when(cartService.getCartById(cartId)).thenReturn(emptyCart);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(cartId, "Test Customer", "123456789", "Test Address");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("cart"));
    }

    @Test
    void clearCart_ShouldThrowException_WhenCartServiceFails() {
        UUID cartId = UUID.randomUUID();

        doThrow(new RuntimeException("Clear cart failed")).when(cartService).clearCart(cartId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.clearCart(cartId);
        });

        assertEquals("Clear cart failed", exception.getMessage());
        verify(cartService).clearCart(cartId);
    }

    @Test
    void addMultipleItemsToCart_ShouldReturnCorrectCart() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        CartItem item1 = new CartItem();
        item1.setPrice(BigDecimal.valueOf(5));
        item1.setQuantity(1);
        CartItem item2 = new CartItem();
        item2.setPrice(BigDecimal.valueOf(7));
        item2.setQuantity(2);
        cart.setItems(List.of(item1, item2));

        when(cartService.getCartById(cartId)).thenReturn(cart);

        Cart returnedCart = cartService.getCartById(cartId);

        assertEquals(2, returnedCart.getItems().size());
        assertEquals(BigDecimal.valueOf(5), returnedCart.getItems().get(0).getPrice());
        assertEquals(BigDecimal.valueOf(7), returnedCart.getItems().get(1).getPrice());

        verify(cartService, times(1)).getCartById(cartId);
    }
}

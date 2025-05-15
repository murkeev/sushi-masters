package murkeev;

import murkeev.dto.CartItemDTO;
import murkeev.exception.EntityNotFoundException;
import murkeev.model.Cart;
import murkeev.model.CartItem;
import murkeev.model.Sushi;
import murkeev.repository.CartItemRepository;
import murkeev.repository.CartRepository;
import murkeev.repository.SushiRepository;
import murkeev.repository.UserRepository;
import murkeev.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private SushiRepository sushiRepository;
    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    CartService cartService;

    @Test
    void getCartById_ShouldReturnCart_WhenFound() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        Cart result = cartService.getCartById(cartId);
        assertEquals(cart, result);
    }

    @Test
    void getCartById_ShouldThrowException_WhenNotFound() {
        UUID cartId = UUID.randomUUID();
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cartService.getCartById(cartId));
    }

    @Test
    void addItemToCart_ShouldAddNewItem_WhenItemNotInCart() {
        UUID cartId = UUID.randomUUID();
        Long sushiId = 1L;
        int quantity = 2;

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());

        Sushi sushi = new Sushi();
        sushi.setId(sushiId);
        sushi.setPrice(BigDecimal.TEN);
        sushi.setImage("img");
        sushi.setName("Sushi");
        sushi.setWeight("150г");

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(sushiRepository.findById(sushiId)).thenReturn(Optional.of(sushi));
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        CartItemDTO result = cartService.addItemToCart(cartId, sushiId, quantity);

        assertEquals("Sushi", result.getName());
        verify(cartItemRepository).save(any());
    }

    @Test
    void getCartTotal_ShouldReturnCorrectTotal() {
        UUID cartId = UUID.randomUUID();
        CartItem item1 = new CartItem();
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(10));

        CartItem item2 = new CartItem();
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(5));

        Cart cart = new Cart();
        cart.setItems(List.of(item1, item2));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        BigDecimal total = cartService.getCartTotal(cartId);
        assertEquals(BigDecimal.valueOf(25), total);
    }

    @Test
    void removeItemFromCart_ShouldRemove_WhenItemExists() {
        UUID cartId = UUID.randomUUID();
        Long sushiId = 1L;

        Sushi sushi = new Sushi();
        sushi.setId(sushiId);

        CartItem item = new CartItem();
        item.setSushi(sushi);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(cartId, sushiId);
        verify(cartRepository).save(cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void clearCart_ShouldRemoveAllItems() {
        UUID cartId = UUID.randomUUID();
        CartItem item = new CartItem();

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.clearCart(cartId);
        verify(cartRepository).save(cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void updateCartItemQuantity_ShouldUpdate_WhenItemExists() {
        UUID cartId = UUID.randomUUID();
        Long itemId = 10L;
        int newQuantity = 3;

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setQuantity(0);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CartItemDTO dto = new CartItemDTO();
        dto.setQuantity(newQuantity);
        when(modelMapper.map(item, CartItemDTO.class)).thenReturn(dto);

        CartItemDTO result = cartService.updateCartItemQuantity(cartId, itemId, newQuantity);

        assertEquals(newQuantity, item.getQuantity());
        assertEquals(newQuantity, result.getQuantity());
        verify(cartRepository).save(cart);
    }


    @Test
    void updateCartItemQuantity_ShouldThrow_WhenItemNotFound() {
        UUID cartId = UUID.randomUUID();
        Long itemId = 99L;

        CartItem otherItem = new CartItem();
        otherItem.setId(1L);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(otherItem)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        assertThrows(EntityNotFoundException.class,
                () -> cartService.updateCartItemQuantity(cartId, itemId, 2));
    }
}
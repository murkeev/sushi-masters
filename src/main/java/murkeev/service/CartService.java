package murkeev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import murkeev.dto.CartItemDTO;
import murkeev.model.Cart;
import murkeev.model.CartItem;
import murkeev.model.Sushi;
import murkeev.model.User;
import murkeev.repository.CartItemRepository;
import murkeev.repository.CartRepository;
import murkeev.repository.SushiRepository;
import murkeev.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SushiRepository sushiRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Cart getCartById(UUID id) {
        return cartRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Cart not found with id: " + id));
    }

    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setItems(new ArrayList<>());
                    Cart saved = cartRepository.save(cart);
                    user.setCart(saved);
                    return saved;
                });
    }

    @Transactional
    public CartItemDTO addItemToCart(UUID cartId, Long sushiId, int quantity) {
        Cart cart = getCartById(cartId);
        Sushi sushi = sushiRepository.findById(sushiId)
                .orElseThrow(() -> new RuntimeException("Sushi not found with id: " + sushiId));

        for (CartItem item : cart.getItems()) {
            if (item.getSushi().getId().equals(sushiId)) {
                item.setQuantity(quantity);
                return modelMapper.map(cartItemRepository.save(item), CartItemDTO.class);
            }
        }
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setSushi(sushi);
        newItem.setQuantity(quantity);
        newItem.setPrice(sushi.getPrice());

        cart.getItems().add(newItem);

        return modelMapper.map(cartItemRepository.save(newItem), CartItemDTO.class);
    }


    @Transactional
    public void removeItemFromCart(UUID cartId, Long itemId) {
        Cart cart = getCartById(cartId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UUID cartId) {
        Cart cart = getCartById(cartId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public BigDecimal getCartTotal(UUID cartId) {
        Cart cart = getCartById(cartId);
        return cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
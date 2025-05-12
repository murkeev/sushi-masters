package murkeev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import murkeev.dto.CartItemDTO;
import murkeev.exception.CartCreationException;
import murkeev.exception.CartItemCreationException;
import murkeev.exception.EntityNotFoundException;
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
@Slf4j
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
        try {
            return cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setUser(user);
                        cart.setItems(new ArrayList<>());
                        Cart saved = cartRepository.save(cart);
                        user.setCart(saved);
                        return saved;
                    });
        } catch (Exception e) {
            log.error("Failed to get or create cart for user with id {}: {}", user.getId(), e.getMessage(), e);
            throw new CartCreationException("Unable to create or retrieve cart for user with id: " + user.getId(), e);
        }
    }


    @Transactional
    public CartItemDTO addItemToCart(UUID cartId, Long sushiId, int quantity) {
        Cart cart = getCartById(cartId);
        Sushi sushi = sushiRepository.findById(sushiId)
                .orElseThrow(() -> new EntityNotFoundException("Sushi not found with id: " + sushiId));
        for (CartItem item : cart.getItems()) {
            if (item.getSushi().getId().equals(sushiId)) {
                item.setQuantity(quantity);
                try {
                    cartItemRepository.save(item);
                    return new CartItemDTO(item);
                } catch (Exception e) {
                    throw new CartItemCreationException("Failed to update item quantity", e);
                }
            }
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setSushi(sushi);
        newItem.setQuantity(quantity);
        newItem.setPrice(sushi.getPrice());
        newItem.setImage(sushi.getImage());
        newItem.setName(sushi.getName());
        newItem.setWeight(sushi.getWeight());

        cart.getItems().add(newItem);
        try {
            CartItem savedItem = cartItemRepository.save(newItem);
            return new CartItemDTO(savedItem);
        } catch (Exception e) {
            throw new CartItemCreationException("Failed to add new item to cart", e);
        }
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

    @Transactional
    public Cart createGuestCart() {
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart getOrCreateGuestCart(UUID cartId) {
        if (cartId != null) {
            return getCartById(cartId);
        }
        return createGuestCart();
    }

    @Transactional
    public CartItemDTO updateCartItemQuantity(UUID cartId, Long itemId, int quantity) {
        try {
            Cart cart = getCartById(cartId);
            CartItem itemToUpdate = null;

            for (CartItem item : cart.getItems()) {
                if (item.getId().equals(itemId)) {
                    itemToUpdate = item;
                    break;
                }
            }

            if (itemToUpdate == null) {
                throw new EntityNotFoundException("Cart item not found with id: " + itemId);
            }

            itemToUpdate.setQuantity(quantity);
            cartRepository.save(cart);

            return modelMapper.map(itemToUpdate, CartItemDTO.class);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Cart item not found with id: " + itemId);
        }
    }
}
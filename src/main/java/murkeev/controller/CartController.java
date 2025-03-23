package murkeev.controller;

import lombok.RequiredArgsConstructor;
import murkeev.dto.CartDTO;
import murkeev.dto.CartItemDTO;
import murkeev.model.Cart;
import murkeev.model.CartItem;
import murkeev.model.User;
import murkeev.service.CartService;
import murkeev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(user);
        return ResponseEntity.ok(new CartDTO(cart));
    }


    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> addItemToCart(@RequestParam Long sushiId, @RequestParam int quantity) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(user);
        CartItemDTO cartItemDTO = cartService.addItemToCart(cart.getId(), sushiId, quantity);

        return ResponseEntity.ok(cartItemDTO);
    }


    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(user);
        System.out.println("cart id: " + cart.getId());
        cartService.removeItemFromCart(cart.getId(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(user);
        cartService.clearCart(cart.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getCartTotal() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(user);
        BigDecimal total = cartService.getCartTotal(cart.getId());
        return ResponseEntity.ok(total);
    }
}
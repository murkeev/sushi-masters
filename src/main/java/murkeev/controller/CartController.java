package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import murkeev.dto.CartDTO;
import murkeev.dto.CartItemDTO;
import murkeev.model.Cart;
import murkeev.model.User;
import murkeev.service.CartService;
import murkeev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "User Cart", description = "Endpoints for managing authenticated user's cart.")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @Operation(summary = "Get current user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CartDTO> getCart() {
        User user = userService.getAuthenticatedUser();
        log.info("Getting cart for user: {}, phone {}", user.getName(), user.getPhone());
        Cart cart = cartService.getOrCreateCart(user);
        log.info("Cart retrieved for user: {}, phone {}", user.getName(), user.getPhone());
        return ResponseEntity.ok(new CartDTO(cart));
    }

    @Operation(summary = "Add item to cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid sushi ID or quantity"),
            @ApiResponse(responseCode = "404", description = "Sushi not found")
    })
    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CartItemDTO> addItemToCart(@RequestParam Long sushiId, @RequestParam int quantity) {
        User user = userService.getAuthenticatedUser();
        log.info("Adding item to cart for user: {}, phone {}, sushi id: {}, quantity: {}",
                user.getName(), user.getPhone(), sushiId, quantity);
        Cart cart = cartService.getOrCreateCart(user);
        log.info("Item successfully added to cart for user: {}, phone {}", user.getName(), user.getPhone());
        CartItemDTO cartItemDTO = cartService.addItemToCart(cart.getId(), sushiId, quantity);

        return ResponseEntity.ok(cartItemDTO);
    }

    @Operation(summary = "Remove item from cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId) {
        User user = userService.getAuthenticatedUser();
        log.info("Removing item from cart for user: {}, phone {}, item id: {}", user.getName(), user.getPhone(), itemId);
        Cart cart = cartService.getOrCreateCart(user);
        log.debug("Cart id: {}", cart.getId());
        cartService.removeItemFromCart(cart.getId(), itemId);
        log.info("Item successfully removed from cart for user: {}, phone {}", user.getName(), user.getPhone());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> clearCart() {
        User user = userService.getAuthenticatedUser();
        log.info("Clearing cart for user: {}, phone {}", user.getName(), user.getPhone());
        Cart cart = cartService.getOrCreateCart(user);
        cartService.clearCart(cart.getId());
        log.info("Cart successfully cleared for user: {}, phone {}", user.getName(), user.getPhone());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get total amount of cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart total calculated"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<BigDecimal> getCartTotal() {
        User user = userService.getAuthenticatedUser();
        log.info("Calculating cart total for user: {}, phone {}", user.getName(), user.getPhone());
        Cart cart = cartService.getOrCreateCart(user);
        BigDecimal total = cartService.getCartTotal(cart.getId());
        log.info("Cart total calculated for user: {}, phone {}: {}", user.getName(), user.getPhone(), total);
        return ResponseEntity.ok(total);
    }
}
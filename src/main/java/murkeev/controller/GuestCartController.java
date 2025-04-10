package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import murkeev.dto.CartDTO;
import murkeev.dto.CartItemDTO;
import murkeev.exception.EntityManipulationException;
import murkeev.exception.EntityNotFoundException;
import murkeev.model.Cart;
import murkeev.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/guest/cart")
@Tag(name = "Guest Cart", description = "Endpoints for managing the guest cart.")
@RequiredArgsConstructor
public class GuestCartController {
    private final CartService cartService;

    @Operation(summary = "Get cart by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = EntityManipulationException.class)))
    })
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable UUID cartId) {
        Cart cart = cartService.getCartById(cartId);
        CartDTO cartDTO = new CartDTO(cart);
        return ResponseEntity.ok()
                .header("Access-Control-Expose-Headers", "X-Cart-Id")
                .header("X-Cart-Id", cart.getId().toString())
                .body(cartDTO);
    }

    @Operation(summary = "Add item to cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = IllegalArgumentException.class))),
            @ApiResponse(responseCode = "404", description = "Sushi or cart not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = EntityManipulationException.class)))
    })
    @PostMapping(value = {"/items", "/{cartId}/items"})
    public ResponseEntity<CartItemDTO> addItemToCart(
            @Parameter(description = "Cart ID (optional - if not provided, new cart will be created)")
            @PathVariable(required = false) UUID cartId,
            @Parameter(description = "ID of the sushi to add", required = true)
            @RequestParam Long sushiId,
            @Parameter(description = "Quantity of items to add", required = true)
            @RequestParam int quantity) {
        Cart cart = cartService.getOrCreateGuestCart(cartId);
        CartItemDTO cartItem = cartService.addItemToCart(cart.getId(), sushiId, quantity);
        return ResponseEntity.ok()
                .header("Access-Control-Expose-Headers", "X-Cart-Id")
                .header("X-Cart-Id", cart.getId().toString())
                .body(cartItem);
    }

    @Operation(summary = "Remove item from cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Cart or item not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = EntityManipulationException.class)))
    })
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @Parameter(description = "Cart ID", required = true) @PathVariable UUID cartId,
            @Parameter(description = "Item ID to remove", required = true) @PathVariable Long itemId) {
        cartService.removeItemFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = EntityManipulationException.class)))
    })
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "Cart ID", required = true) @PathVariable UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get cart total")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total calculated successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = EntityManipulationException.class)))
    })
    @GetMapping("/{cartId}/total")
    public ResponseEntity<BigDecimal> getCartTotal(
            @Parameter(description = "Cart ID", required = true) @PathVariable UUID cartId) {
        BigDecimal total = cartService.getCartTotal(cartId);
        return ResponseEntity.ok(total);
    }


    @Operation(summary = "Update item quantity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
            @ApiResponse(responseCode = "204", description = "Item removed due to quantity 0"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity",
                    content = @Content(schema = @Schema(implementation = IllegalArgumentException.class))),
            @ApiResponse(responseCode = "404", description = "Cart or item not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = EntityManipulationException.class)))
    })
    @PatchMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<?> updateItemQuantity(
            @Parameter(description = "Cart ID", required = true) @PathVariable UUID cartId,
            @Parameter(description = "Item ID to update", required = true) @PathVariable Long itemId,
            @Parameter(description = "New quantity", required = true) @RequestParam int quantity) {

        CartItemDTO updatedItem = cartService.updateCartItemQuantity(cartId, itemId, quantity);
        if (updatedItem == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(updatedItem);
    }
}
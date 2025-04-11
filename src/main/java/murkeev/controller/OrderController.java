package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import murkeev.dto.OrderCreateRequest;
import murkeev.dto.OrderDTO;
import murkeev.exception.EntityNotFoundException;
import murkeev.model.Order;
import murkeev.model.User;
import murkeev.service.OrderService;
import murkeev.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Endpoints for managing user orders.")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Create a new order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cart not found for user",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody @Parameter(description = "Order create request", required = true)
            OrderCreateRequest request) {
        try {
            log.info("Creating order for request: {}", request);
            User user = userService.getAuthenticatedUser();

            if (user.getCart() == null) {
                log.error("Cart not found for user: {}", user.getPhone());
                throw new EntityNotFoundException("User has no cart");
            }

            Order order = orderService.createOrder(
                    user.getCart().getId(),
                    request.getCustomerName(),
                    request.getCustomerPhone(),
                    request.getDeliveryAddress()
            );
            log.info("Order created successfully with ID: {}", order.getId());
            return ResponseEntity.ok(new OrderDTO(order));
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            throw e;
        }
    }
    @Operation(summary = "Get order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = EntityNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@Parameter(description = "ID of the order", required = true)
                                                 @PathVariable UUID orderId) {
        try {
            log.info("Getting order by ID: {}", orderId);
            Order order = orderService.getOrderById(orderId);
            log.info("Retrieved order: {}", orderId);
            return ResponseEntity.ok(new OrderDTO(order));
        } catch (Exception e) {
            log.error("Error retrieving order {}: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Get all orders for authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        User user = userService.getAuthenticatedUser();
        List<Order> orders = orderService.getOrdersByPhone(user.getPhone());
        List<OrderDTO> orderDTOs = orders.stream()
                .map(OrderDTO::new)
                .toList();
        return ResponseEntity.ok(orderDTOs);
    }
}
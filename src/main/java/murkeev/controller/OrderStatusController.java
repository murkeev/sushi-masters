package murkeev.controller;

import murkeev.service.OrderStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderStatusController {

    private final OrderStatusService statusService;

    public OrderStatusController(OrderStatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/{id}/status")
    public DeferredResult<String> pollOrderStatus(@PathVariable UUID id) {
        return statusService.getStatusWithPolling(id);
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable UUID id, @RequestBody String status) {
        statusService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}

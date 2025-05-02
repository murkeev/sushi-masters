package murkeev.service;

import murkeev.model.Order;
import murkeev.polling.PollingRequestRegistry;
import murkeev.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final PollingRequestRegistry registry;

    public OrderStatusService(OrderRepository orderRepository, PollingRequestRegistry registry) {
        this.orderRepository = orderRepository;
        this.registry = registry;
    }

    public DeferredResult<String> getStatusWithPolling(UUID orderId) {
        DeferredResult<String> output = new DeferredResult<>(30000L, "NO_UPDATE");

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresent(order -> {
            registry.register(orderId, output);
        });

        return output;
    }

    public void updateStatus(UUID orderId, String newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        optionalOrder.ifPresent(order -> {
            order.setStatus(newStatus);
            orderRepository.save(order);

            if (registry.hasPendingRequest(orderId)) {
                registry.complete(orderId, newStatus);
            }
        });
    }
}

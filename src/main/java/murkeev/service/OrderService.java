package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.repository.OrderRepository;
import murkeev.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
}

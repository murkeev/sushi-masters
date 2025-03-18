package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderItemService {
    private final OrderItemRepository oiRepository;
}

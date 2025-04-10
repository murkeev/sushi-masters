package murkeev.repository;

import murkeev.model.Order;
import murkeev.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(String phone);
}
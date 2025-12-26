package agriculture2.demo.repository;

import agriculture2.demo.entities.orders; // Import your Order entity
import agriculture2.demo.entities.users; // Import your User entity
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// FIX: This MUST be an INTERFACE that extends JpaRepository
// It manages the 'orders' entity and uses 'Long' as the ID type.
public interface OrderRepo extends JpaRepository<orders, Long> {
    
    // Custom query method required by your service logic:
    List<orders> findByBuyer(users buyer);
}
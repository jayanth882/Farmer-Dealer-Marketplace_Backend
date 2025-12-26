package agriculture2.demo.repository;

import agriculture2.demo.entities.product; // Import your Product entity
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// FIX: Define as an interface, extending JpaRepository.
// <product, Long> tells Spring this repository manages the 'product' entity 
// and that its primary key type is 'Long'.
public interface ProductRepo extends JpaRepository<product, Long> {
    
    // Custom query method from your original plan:
    List<product> findByFarmerId(Long farmerId);
}
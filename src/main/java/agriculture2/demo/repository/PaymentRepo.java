package agriculture2.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import agriculture2.demo.entities.Payment;
import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.users;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    List<Payment> findByBuyer(users buyer);

    List<Payment> findByFarmer(users farmer);

    List<Payment> findByAuction(Auction auction);
}

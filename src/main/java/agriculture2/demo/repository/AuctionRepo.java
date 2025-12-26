package agriculture2.demo.repository;

import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepo extends JpaRepository<Auction, Long> {

    // All auctions which are still active (time not over & status ACTIVE)
    @Query("select a from Auction a " +
           "where a.endTime > :now and a.status = 'ACTIVE'")
    List<Auction> findActiveAuctions(@Param("now") LocalDateTime now);

    // All auctions for a specific farmer (used for farmer dashboard)
    List<Auction> findByProduct_Farmer(users farmer);

    // All auctions with given status whose endTime is in the past (used by scheduler)
    List<Auction> findByStatusAndEndTimeBefore(String status, LocalDateTime endTime);

    // All auctions with a given status for a specific farmer email (completed sales)
    @Query("select a from Auction a " +
           "where a.status = :status and a.product.farmer.email = :email")
    List<Auction> findByStatusAndFarmerEmail(@Param("status") String status,
                                             @Param("email") String email);
}

package agriculture2.demo.repository;

import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepo extends JpaRepository<Bid, Long> {

    // All bids for an auction, highest first
    List<Bid> findByAuctionOrderByAmountDesc(Auction auction);

    // Single highest bid for an auction
    Bid findTopByAuctionOrderByAmountDesc(Auction auction);
}

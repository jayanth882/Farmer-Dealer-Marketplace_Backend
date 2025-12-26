package agriculture2.demo.service;

import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.Bid;
import agriculture2.demo.repository.AuctionRepo;
import agriculture2.demo.repository.BidRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    private final AuctionRepo auctionRepo;
    private final BidRepo bidRepo;
    private final OrderService orderService;

    // Manual constructor (no Lombok)
    public AuctionScheduler(AuctionRepo auctionRepo,
                            BidRepo bidRepo,
                            OrderService orderService) {
        this.auctionRepo = auctionRepo;
        this.bidRepo = bidRepo;
        this.orderService = orderService;
    }

    /**
     * Runs every 10 seconds and:
     * 1) Finds all ACTIVE auctions whose endTime is in the past.
     * 2) For each auction:
     *      - Finds the highest bid (if any)
     *      - Creates an order from that bid
     *      - Sets finalPrice and winningBuyer on the auction
     *      - Marks auction as COMPLETED
     *      - Sets product quantity to 0 (OUT OF STOCK)
     */
    @Scheduled(fixedRate = 10000)
    public void closeFinishedAuctions() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[Scheduler] running at " + now);

        // 1) Get all ACTIVE auctions whose endTime is in the past
        List<Auction> endedAuctions =
                auctionRepo.findByStatusAndEndTimeBefore("ACTIVE", now);

        if (endedAuctions.isEmpty()) {
            System.out.println("[Scheduler] no auctions to close right now");
            return;
        }

        // 2) For each auction: find highest bid, create order, update status
        for (Auction auction : endedAuctions) {
            System.out.println("[Scheduler] processing auction id = " + auction.getId());

            // Highest bid for this auction (can be null)
            Bid highestBid = bidRepo.findTopByAuctionOrderByAmountDesc(auction);

            if (highestBid != null) {
                System.out.println(
                        "[Scheduler] highest bid = " + highestBid.getAmount()
                                + " by " + highestBid.getBuyer().getEmail()
                );

                // Create order + order_items from this winning bid
                orderService.createOrderFromAuction(auction, highestBid);

                // Set final price and winning buyer on auction
                auction.setFinalPrice(highestBid.getAmount());
                auction.setWinningBuyer(highestBid.getBuyer());
            } else {
                System.out.println(
                        "[Scheduler] auction " + auction.getId()
                                + " has no bids, closing without order"
                );
            }

            // Mark auction as completed
            auction.setStatus("COMPLETED");

            // Mark product as OUT OF STOCK (quantity 0) for all buyers
            if (auction.getProduct() != null) {
                auction.getProduct().setQuantity(0);
            }
        }

        // 3) Save all updated auctions (and their products)
        auctionRepo.saveAll(endedAuctions);
        System.out.println("[Scheduler] finished closing " + endedAuctions.size() + " auctions");
    }
}

package agriculture2.demo.service;

import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.Bid;
import agriculture2.demo.entities.OrderItem;
import agriculture2.demo.entities.orders;
import agriculture2.demo.entities.product;
import agriculture2.demo.entities.users;
import agriculture2.demo.repository.AuctionRepo;
import agriculture2.demo.repository.BidRepo;
import agriculture2.demo.repository.OrderItemRepo;
import agriculture2.demo.repository.OrderRepo;
import agriculture2.demo.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final AuctionRepo auctionRepo;
    private final BidRepo bidRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;

    @Autowired
    public OrderService(
            AuctionRepo auctionRepo,
            BidRepo bidRepo,
            OrderRepo orderRepo,
            OrderItemRepo orderItemRepo,
            ProductRepo productRepo
    ) {
        this.auctionRepo = auctionRepo;
        this.bidRepo = bidRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
    }

    /**
     * Called by the scheduler.
     * Finds all ACTIVE auctions whose endTime is in the past
     * and completes each of them.
     */
    @Transactional
    public void completeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> expiredAuctions =
                auctionRepo.findByStatusAndEndTimeBefore("ACTIVE", now);

        for (Auction auction : expiredAuctions) {
            completeSingleAuction(auction);
        }
    }

    /**
     * Completes one auction:
     *  - Finds winning bid
     *  - Creates order + order_item
     *  - Updates auction finalPrice + status
     *  - Marks product as out of stock
     */
    @Transactional
    public void completeSingleAuction(Auction auction) {
        // Reload from DB to be safe
        Auction managedAuction = auctionRepo.findById(auction.getId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // If already completed, do nothing
        if (!"ACTIVE".equalsIgnoreCase(managedAuction.getStatus())) {
            return;
        }

        // 1) Find winning bid (highest amount)
        Bid winningBid = bidRepo.findTopByAuctionOrderByAmountDesc(managedAuction);

        // If no bids were placed: just close the auction, keep product as-is
        if (winningBid == null) {
            managedAuction.setStatus("COMPLETED");
            managedAuction.setFinalPrice(null);
            auctionRepo.save(managedAuction);
            return;
        }

        product prod = managedAuction.getProduct();
        users buyer = winningBid.getBuyer();

        // 2) Create order
        orders order = new orders();
        order.setBuyer(buyer);
        order.setTotalAmount(winningBid.getAmount());
        orders savedOrder = orderRepo.save(order);

        // 3) Create order item for the product
        OrderItem item = new OrderItem();
        item.setOrder(savedOrder);
        item.setProduct(prod);
        // If quantity is null, assume 1
        Integer quantity = (prod.getQuantity() != null) ? prod.getQuantity() : 1;
        item.setQuantity(quantity);
        item.setPrice(winningBid.getAmount());

        orderItemRepo.save(item);

        // 4) Mark auction as completed and store final price
        managedAuction.setStatus("COMPLETED");
        managedAuction.setFinalPrice(winningBid.getAmount());
        auctionRepo.save(managedAuction);

        // 5) Mark product as out of stock (quantity = 0)
        prod.setQuantity(0);
        productRepo.save(prod);
    }
    // Called by AuctionScheduler when auction ends with a highest bid
    public void createOrderFromAuction(Auction auction, Bid highestBid) {
        System.out.println("✅ [OrderService] Creating order for auction "
                + auction.getId() + " for buyer "
                + highestBid.getBuyer().getEmail()
                + " amount " + highestBid.getAmount());

        // TODO: later we will implement real order + order_items creation here.
    }

}


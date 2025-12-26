package agriculture2.demo.controller;

import agriculture2.demo.dto.BidResponse;
import agriculture2.demo.dto.CreateBidRequest;
import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.Bid;
import agriculture2.demo.entities.users;
import agriculture2.demo.repository.AuctionRepo;
import agriculture2.demo.repository.BidRepo;
import agriculture2.demo.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidRepo bidRepo;
    private final AuctionRepo auctionRepo;
    private final UserRepo userRepo;

    public BidController(BidRepo bidRepo,
                         AuctionRepo auctionRepo,
                         UserRepo userRepo) {
        this.bidRepo = bidRepo;
        this.auctionRepo = auctionRepo;
        this.userRepo = userRepo;
    }

    // ------------------------------------------------------------------
    // BUYER: Place a bid
    // Endpoint: POST /api/bids
    // Body (JSON):
    // {
    //   "auctionId": 1,
    //   "buyerEmail": "buyer@example.com",
    //   "amount": 150.0
    // }
    // ------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> placeBid(@RequestBody CreateBidRequest req) {

        Auction auction = auctionRepo.findById(req.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(auction.getEndTime())) {
            return ResponseEntity.badRequest().body("Auction has already ended");
        }
        if (!"ACTIVE".equals(auction.getStatus())) {
            return ResponseEntity.badRequest().body("Auction is not active");
        }

        users buyer = userRepo.findByEmail(req.getBuyerEmail())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // --- Validate minimum amount: > base price AND > current highest ---
        Bid highest = bidRepo.findTopByAuctionOrderByAmountDesc(auction);
        double minAllowed = auction.getBasePrice();
        if (highest != null && highest.getAmount() > minAllowed) {
            minAllowed = highest.getAmount();
        }

        if (req.getAmount() == null || req.getAmount() <= minAllowed) {
            return ResponseEntity.badRequest()
                    .body("Bid must be greater than " + minAllowed);
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBuyer(buyer);
        bid.setAmount(req.getAmount());
        bid.setCreatedAt(now);
        bidRepo.save(bid);

        // Update current leading info in auction
        auction.setFinalPrice(bid.getAmount());
        auction.setWinningBuyer(buyer);
        auctionRepo.save(auction);

        BidResponse resp = new BidResponse(
                bid.getId(),
                auction.getId(),
                buyer.getEmail(),
                bid.getAmount(),
                bid.getCreatedAt()
        );

        return ResponseEntity.ok(resp);
    }

    // ------------------------------------------------------------------
    // View all bids for a given auction (farmer or buyers)
    // Endpoint: GET /api/bids/auction/{auctionId}
    // ------------------------------------------------------------------
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getBidsForAuction(@PathVariable Long auctionId) {
        Auction auction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        List<Bid> bids = bidRepo.findByAuctionOrderByAmountDesc(auction);

        List<BidResponse> result = bids.stream()
                .map(b -> new BidResponse(
                        b.getId(),
                        auction.getId(),
                        b.getBuyer().getEmail(),
                        b.getAmount(),
                        b.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

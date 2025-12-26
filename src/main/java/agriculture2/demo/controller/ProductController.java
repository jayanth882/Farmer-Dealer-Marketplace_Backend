package agriculture2.demo.controller;

import agriculture2.demo.dto.AuctionViewDto;
import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.Bid;
import agriculture2.demo.entities.product;
import agriculture2.demo.entities.users;
import agriculture2.demo.repository.AuctionRepo;
import agriculture2.demo.repository.BidRepo;
import agriculture2.demo.repository.ProductRepo;
import agriculture2.demo.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final AuctionRepo auctionRepo;
    private final BidRepo bidRepo;

    // Folder inside app/container where images will be stored
    private final Path uploadDir = Paths.get("uploads");

    public ProductController(ProductRepo productRepo,
                             UserRepo userRepo,
                             AuctionRepo auctionRepo,
                             BidRepo bidRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.auctionRepo = auctionRepo;
        this.bidRepo = bidRepo;
    }

    // ----------------------------------------------------------------------
    // (A) FARMER: Create product + upload image + create auction
    // Endpoint: POST /api/products
    // Body: form-data (name, description, price, quantity, farmerEmail,
    //                  auctionDurationMinutes, image (file))
    // ----------------------------------------------------------------------
    @PostMapping("/products")
    public ResponseEntity<?> createProductWithAuction(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("farmerEmail") String farmerEmail,
            @RequestParam("auctionDurationMinutes") Integer auctionDurationMinutes,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        users farmer = userRepo.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found: " + farmerEmail));

        // --- Save image file if provided ---
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path targetPath = uploadDir.resolve(fileName).toAbsolutePath();
            image.transferTo(targetPath.toFile());
            imagePath = fileName; // we store just file name in DB
        }

        // --- Create product ---
        product p = new product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);   // used as base price
        p.setQuantity(quantity);
        p.setFarmer(farmer);
        p.setImagePath(imagePath);

        productRepo.save(p);

        // --- Create auction for this product ---
        Auction auction = new Auction();
        auction.setProduct(p);
        auction.setBasePrice(price);
        LocalDateTime now = LocalDateTime.now();
        auction.setStartTime(now);
        auction.setEndTime(now.plusMinutes(auctionDurationMinutes));
        auction.setStatus("ACTIVE");
        auctionRepo.save(auction);

        return ResponseEntity.ok("Product and auction created successfully");
    }

    // ----------------------------------------------------------------------
    // (B) BUYER: Get all active auctions
    // Endpoint: GET /api/auctions/active
    // ----------------------------------------------------------------------
    @GetMapping("/auctions/active")
    public ResponseEntity<List<AuctionViewDto>> getActiveAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> auctions = auctionRepo.findActiveAuctions(now);
        List<AuctionViewDto> result = new ArrayList<>();

        for (Auction a : auctions) {
            Bid highest = bidRepo.findTopByAuctionOrderByAmountDesc(a);
            Double highestAmount = highest != null ? highest.getAmount() : null;

            product p = a.getProduct();

            AuctionViewDto dto = new AuctionViewDto(
                    a.getId(),
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getImagePath(),
                    a.getBasePrice(),
                    highestAmount,
                    a.getEndTime(),
                    a.getStatus()
            );
            result.add(dto);
        }

        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------------
    // (C) FARMER: View all auctions for this farmer (Active + Completed)
    // Endpoint: GET /api/farmers/{email}/auctions
    // Frontend can use this for:
    //   - Active Auctions tab
    //   - Completed Sales tab (when endTime < now or status = COMPLETED)
    // ----------------------------------------------------------------------
    @GetMapping("/farmers/{email}/auctions")
    public ResponseEntity<List<AuctionViewDto>> getFarmerAuctions(@PathVariable("email") String email) {
        users farmer = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found: " + email));

        List<Auction> auctions = auctionRepo.findByProduct_Farmer(farmer);
        List<AuctionViewDto> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Auction a : auctions) {
            Bid highest = bidRepo.findTopByAuctionOrderByAmountDesc(a);
            Double highestAmount = highest != null ? highest.getAmount() : null;

            product p = a.getProduct();

            // Derive status for frontend: if time over, treat as COMPLETED
            String status = a.getStatus();
            if ("ACTIVE".equals(status) && now.isAfter(a.getEndTime())) {
                status = "COMPLETED";
            }

            AuctionViewDto dto = new AuctionViewDto(
                    a.getId(),
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getImagePath(),
                    a.getBasePrice(),
                    highestAmount,
                    a.getEndTime(),
                    status
            );
            result.add(dto);
        }

        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------------
    // (D) Optional: simple products list (if you need plain products anywhere)
    // Endpoint: GET /api/products
    // ----------------------------------------------------------------------
    @GetMapping("/products")
    public ResponseEntity<List<product>> getAllProducts() {
        return ResponseEntity.ok(productRepo.findAll());
    }
}

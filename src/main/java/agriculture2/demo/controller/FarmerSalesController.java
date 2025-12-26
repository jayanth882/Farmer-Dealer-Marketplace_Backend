package agriculture2.demo.controller;

import agriculture2.demo.dto.CompletedSaleDto;
import agriculture2.demo.entities.Auction;
import agriculture2.demo.repository.AuctionRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "*")
public class FarmerSalesController {

    private final AuctionRepo auctionRepo;

    public FarmerSalesController(AuctionRepo auctionRepo) {
        this.auctionRepo = auctionRepo;
    }

    /**
     * FARMER – COMPLETED SALES
     *
     * Example:
     *   GET /api/farmers/test123@gmail.com/completed-sales
     */
    @GetMapping("/{email}/completed-sales")
    public List<CompletedSaleDto> getCompletedSales(@PathVariable("email") String farmerEmail) {

        // All COMPLETED auctions for this farmer
        List<Auction> completedAuctions =
                auctionRepo.findByStatusAndFarmerEmail("COMPLETED", farmerEmail);

        // Map to DTO
        return completedAuctions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CompletedSaleDto toDto(Auction auction) {
        CompletedSaleDto dto = new CompletedSaleDto();

        dto.setAuctionId(auction.getId());
        dto.setBasePrice(auction.getBasePrice());
        dto.setFinalPrice(auction.getFinalPrice());
        dto.setEndTime(auction.getEndTime());

        if (auction.getProduct() != null) {
            dto.setProductId(auction.getProduct().getId());
            dto.setProductName(auction.getProduct().getName());
            dto.setProductDescription(auction.getProduct().getDescription());
            dto.setImagePath(auction.getProduct().getImagePath());
            dto.setQuantity(auction.getProduct().getQuantity());
        }

        if (auction.getWinningBuyer() != null) {
            dto.setBuyerEmail(auction.getWinningBuyer().getEmail());
        }

        return dto;
    }
}

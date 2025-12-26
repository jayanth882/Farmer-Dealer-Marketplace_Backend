package agriculture2.demo.dto;

import java.time.LocalDateTime;

public class AuctionViewDto {

    private Long auctionId;
    private Long productId;
    private String productName;
    private String productDescription;
    private String imagePath;
    private Double basePrice;
    private Double highestBid;
    private LocalDateTime endTime;
    private String status;

    public AuctionViewDto() {
    }

    public AuctionViewDto(Long auctionId,
                          Long productId,
                          String productName,
                          String productDescription,
                          String imagePath,
                          Double basePrice,
                          Double highestBid,
                          LocalDateTime endTime,
                          String status) {
        this.auctionId = auctionId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.imagePath = imagePath;
        this.basePrice = basePrice;
        this.highestBid = highestBid;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Double highestBid) {
        this.highestBid = highestBid;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package agriculture2.demo.dto;

import java.time.LocalDateTime;

public class BidResponse {

    private Long bidId;
    private Long auctionId;
    private String buyerEmail;
    private Double amount;
    private LocalDateTime createdAt;

    public BidResponse() {
    }

    public BidResponse(Long bidId,
                       Long auctionId,
                       String buyerEmail,
                       Double amount,
                       LocalDateTime createdAt) {
        this.bidId = bidId;
        this.auctionId = auctionId;
        this.buyerEmail = buyerEmail;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

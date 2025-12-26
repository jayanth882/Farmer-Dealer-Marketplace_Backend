package agriculture2.demo.dto;

// Request to create a payment for a winning auction
public class PaymentCreateRequest {

    // Auction for which payment is being done
    private Long auctionId;

    // "CARD", "UPI", "WALLET" (PhonePe, GPay, Paytm etc will all be WALLET)
    private String paymentMethod;

    public PaymentCreateRequest() {}

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

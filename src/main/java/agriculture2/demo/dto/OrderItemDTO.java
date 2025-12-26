package agriculture2.demo.dto;

// Import Double since you used it in your screenshot
public class OrderItemDTO {
    private String productName;
    private Integer quantity;
    private Double price;

    // Getters and Setters (REQUIRED)
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
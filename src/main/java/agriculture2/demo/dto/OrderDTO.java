package agriculture2.demo.dto;

import java.util.List;

public class OrderDTO {
    private Long id;
    private String buyerName;
    private Double totalAmount; 
    private List<OrderItemDTO> items;

    // Default Constructor (REQUIRED)
    public OrderDTO() {}

    // Getters and Setters (REQUIRED)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
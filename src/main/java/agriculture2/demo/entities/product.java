package agriculture2.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class product {

    // Payout for THIS product:
    // DEFAULT -> use farmer's default account from users.defaultPayout*
    // UPI     -> use payoutUpiId
    // BANK    -> use bank fields below
    public enum ProductPayoutMethod {
        DEFAULT,
        UPI,
        BANK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private users farmer;  // farmer who owns this product

    // ------------- NEW FIELDS: PAYOUT SETTINGS PER PRODUCT -------------

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_method", length = 20)
    private ProductPayoutMethod payoutMethod = ProductPayoutMethod.DEFAULT;

    // If payoutMethod == UPI
    @Column(name = "payout_upi_id")
    private String payoutUpiId;

    // If payoutMethod == BANK
    @Column(name = "payout_account_holder_name")
    private String payoutAccountHolderName;

    @Column(name = "payout_account_number")
    private String payoutAccountNumber;

    @Column(name = "payout_ifsc_code")
    private String payoutIfscCode;

    @Column(name = "payout_bank_name")
    private String payoutBankName;

    // --- Constructors ---
    public product() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public users getFarmer() { return farmer; }
    public void setFarmer(users farmer) { this.farmer = farmer; }

    // ---- NEW FIELDS: GETTERS & SETTERS ----

    public ProductPayoutMethod getPayoutMethod() {
        return payoutMethod;
    }

    public void setPayoutMethod(ProductPayoutMethod payoutMethod) {
        this.payoutMethod = payoutMethod;
    }

    public String getPayoutUpiId() {
        return payoutUpiId;
    }

    public void setPayoutUpiId(String payoutUpiId) {
        this.payoutUpiId = payoutUpiId;
    }

    public String getPayoutAccountHolderName() {
        return payoutAccountHolderName;
    }

    public void setPayoutAccountHolderName(String payoutAccountHolderName) {
        this.payoutAccountHolderName = payoutAccountHolderName;
    }

    public String getPayoutAccountNumber() {
        return payoutAccountNumber;
    }

    public void setPayoutAccountNumber(String payoutAccountNumber) {
        this.payoutAccountNumber = payoutAccountNumber;
    }

    public String getPayoutIfscCode() {
        return payoutIfscCode;
    }

    public void setPayoutIfscCode(String payoutIfscCode) {
        this.payoutIfscCode = payoutIfscCode;
    }

    public String getPayoutBankName() {
        return payoutBankName;
    }

    public void setPayoutBankName(String payoutBankName) {
        this.payoutBankName = payoutBankName;
    }
}

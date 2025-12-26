package agriculture2.demo.entities;

import java.util.Set;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class users {

    // --------- NESTED ENUM FOR DEFAULT PAYOUT METHOD ----------
    // NONE  : no default payout set
    // UPI   : use defaultUpiId
    // BANK  : use default bank account fields
    public enum PayoutMethod {
        NONE,
        UPI,
        BANK
    }

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String username;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name="role")
    private Set<String> roles;

    // ------------- NEW FIELDS: DEFAULT PAYOUT SETTINGS -------------

    @Enumerated(EnumType.STRING)
    @Column(name = "default_payout_method", length = 20)
    private PayoutMethod defaultPayoutMethod = PayoutMethod.NONE;

    // Default UPI ID (for receiving payments)
    @Column(name = "default_upi_id")
    private String defaultUpiId;

    // Default bank details
    @Column(name = "default_account_holder_name")
    private String defaultAccountHolderName;

    @Column(name = "default_account_number")
    private String defaultAccountNumber;

    @Column(name = "default_ifsc_code")
    private String defaultIfscCode;

    @Column(name = "default_bank_name")
    private String defaultBankName;

    // ---------------- CONSTRUCTORS ----------------

    // Default Constructor (Required by JPA)
    public users() {
    }

    // ---------------- GETTERS & SETTERS ----------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * It must return the type of the 'roles' field (Set<String>).
     * @return Set<String> of user roles.
     */
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // ------ NEW FIELDS: GETTERS & SETTERS ------

    public PayoutMethod getDefaultPayoutMethod() {
        return defaultPayoutMethod;
    }

    public void setDefaultPayoutMethod(PayoutMethod defaultPayoutMethod) {
        this.defaultPayoutMethod = defaultPayoutMethod;
    }

    public String getDefaultUpiId() {
        return defaultUpiId;
    }

    public void setDefaultUpiId(String defaultUpiId) {
        this.defaultUpiId = defaultUpiId;
    }

    public String getDefaultAccountHolderName() {
        return defaultAccountHolderName;
    }

    public void setDefaultAccountHolderName(String defaultAccountHolderName) {
        this.defaultAccountHolderName = defaultAccountHolderName;
    }

    public String getDefaultAccountNumber() {
        return defaultAccountNumber;
    }

    public void setDefaultAccountNumber(String defaultAccountNumber) {
        this.defaultAccountNumber = defaultAccountNumber;
    }

    public String getDefaultIfscCode() {
        return defaultIfscCode;
    }

    public void setDefaultIfscCode(String defaultIfscCode) {
        this.defaultIfscCode = defaultIfscCode;
    }

    public String getDefaultBankName() {
        return defaultBankName;
    }

    public void setDefaultBankName(String defaultBankName) {
        this.defaultBankName = defaultBankName;
    }
}

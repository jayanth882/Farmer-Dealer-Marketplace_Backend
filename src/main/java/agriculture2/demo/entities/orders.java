	package agriculture2.demo.entities;
	
	import jakarta.persistence.*;
	import java.util.List;
	
	// FIX 1: REMOVE the incorrect import: import org.apache.catalina.User; 
	// FIX 2: We rely on the existing entity 'users' in the same package.
	
	@Entity
	@Table(name = "orders")
	public class orders{
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	
	    @ManyToOne
	    // FIX 3: Change type from Catalina User to your 'users' entity
	    private users buyer; 
	
	    private Double totalAmount;
	
	    @OneToMany(cascade = CascadeType.ALL)
	    @JoinColumn(name = "order_id")
	    private List<OrderItem> items;
	
	    // Default constructor
	    public orders() {}
	
	    // Getters and setters
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	
	    // FIX 4: Update method signature to use your 'users' entity type
	    public users getBuyer() { return buyer; }
	    public void setBuyer(users buyer) { this.buyer = buyer; }
	
	    public Double getTotalAmount() { return totalAmount; }
	    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
	
	    public List<OrderItem> getItems() { return items; }
	    public void setItems(List<OrderItem> items) { this.items = items; }
	}
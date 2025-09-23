package agriculture2.demo.entities;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="users")

public class users {
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}

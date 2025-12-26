package agriculture2.demo.service;

import agriculture2.demo.repository.ProductRepo;
import agriculture2.demo.entities.product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    // FIX: Renamed instance variable to 'productRepo' (lowercase 'p')
    private ProductRepo productRepo; // <-- Change 'ProductRepo' to 'productRepo' here

    public List<product> getAllProducts() {
        // FIX: Use the new lowercase instance name
        return productRepo.findAll(); 
    }

    public product saveProduct(product p) {
        // FIX: Use the new lowercase instance name
        return productRepo.save(p);
    }
}
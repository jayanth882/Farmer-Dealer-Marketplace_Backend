package agriculture2.demo.controller;

import agriculture2.demo.entities.*; 
import agriculture2.demo.repository.OrderRepo; 
import agriculture2.demo.repository.ProductRepo; 
import agriculture2.demo.repository.UserRepo; 
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepo orderRepository;
    private final ProductRepo productRepository;
    private final UserRepo userRepository;

    public OrderController(OrderRepo orderRepository, ProductRepo productRepository, UserRepo userRepository){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<Long,Integer> items, @AuthenticationPrincipal UserDetails userDetails){
        
        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_BUYER"))) {
            return ResponseEntity.status(403).body("Only buyers can place orders");
        }
        
        users buyer = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;
        
        for (Map.Entry<Long,Integer> e : items.entrySet()) {
            product p = productRepository.findById(e.getKey()).orElseThrow();
            int qty = e.getValue();
            
            OrderItem oi = new OrderItem();
            oi.setProduct(p);
            oi.setQuantity(qty);
            oi.setPrice(p.getPrice());
            orderItems.add(oi);
            
            total += p.getPrice() * qty;
            
            p.setQuantity(p.getQuantity() - qty);
            productRepository.save(p);
        }
        
        orders order = new orders();
        order.setBuyer(buyer);
        order.setItems(orderItems);
        order.setTotalAmount(total);
        orderRepository.save(order);
        
        return ResponseEntity.ok(order);
    }
}
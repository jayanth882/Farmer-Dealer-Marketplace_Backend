package agriculture2.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import agriculture2.demo.dto.FarmerPayoutRequest;
import agriculture2.demo.dto.FarmerPayoutResponse;
import agriculture2.demo.dto.PaymentResponse;
import agriculture2.demo.service.PaymentService;

@RestController
@RequestMapping("/api/farmer")
public class FarmerPayoutController {

    private final PaymentService paymentService;

    public FarmerPayoutController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ---------------------------------------------------------------------
    // Get current default payout settings for this logged-in farmer
    // ---------------------------------------------------------------------
    @GetMapping("/payout")
    public ResponseEntity<FarmerPayoutResponse> getPayout(Authentication auth) {

        // If request is not authenticated, return 401 instead of NPE
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName(); // in your app, username is email

        FarmerPayoutResponse response =
                paymentService.getFarmerPayoutDetails(email);

        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------------------
    // Update default payout settings for this logged-in farmer
    // ---------------------------------------------------------------------
    @PutMapping("/payout")
    public ResponseEntity<FarmerPayoutResponse> updatePayout(
            @RequestBody FarmerPayoutRequest request,
            Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();

        FarmerPayoutResponse updated =
                paymentService.updateFarmerPayoutDetails(email, request);

        return ResponseEntity.ok(updated);
    }

    // ---------------------------------------------------------------------
    // Get list of payments this farmer has received
    // ---------------------------------------------------------------------
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> farmerPayments(
            Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();

        List<PaymentResponse> payments =
                paymentService.getPaymentsForFarmer(email);

        return ResponseEntity.ok(payments);
    }
}

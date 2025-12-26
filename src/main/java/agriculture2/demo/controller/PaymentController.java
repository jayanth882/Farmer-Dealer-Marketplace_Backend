package agriculture2.demo.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import agriculture2.demo.dto.PaymentCreateRequest;
import agriculture2.demo.dto.PaymentResponse;
import agriculture2.demo.service.PaymentService;

/**
 * PaymentController
 *
 * This file contains the normal authenticated endpoints plus two local-only
 * debugging endpoints to help you test payments during development:
 *
 *  - POST /api/payments/debug/create-diagnostics
 *      Calls paymentService.createPayment(...) with a test buyer email and returns
 *      either the PaymentResponse (200) or a detailed diagnostic body (500) with
 *      error + stacktrace.
 *
 *  - POST /api/payments/debug/create-as/{email}
 *      Bypasses authentication and calls paymentService.createPayment(email, req).
 *      Use only locally for quick testing. Remove before shipping.
 *
 * IMPORTANT: Remove or secure these debug endpoints before deploying to production.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * BUYER: Create payment for a winning auction (authenticated).
     * POST /api/payments
     * Body: PaymentCreateRequest { auctionId, paymentMethod }
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentCreateRequest request,
            Authentication authentication
    ) {
        String buyerEmail = authentication.getName();
        PaymentResponse resp = paymentService.createPayment(buyerEmail, request);
        return ResponseEntity.ok(resp);
    }

    /**
     * Mock/gateway callback to mark payment success.
     * POST /api/payments/{paymentId}/success?gatewayPaymentId=...
     */
    @PostMapping("/{paymentId}/success")
    public ResponseEntity<PaymentResponse> markPaymentSuccess(
            @PathVariable Long paymentId,
            @RequestParam String gatewayPaymentId
    ) {
        PaymentResponse resp = paymentService.markPaymentSuccess(paymentId, gatewayPaymentId);
        return ResponseEntity.ok(resp);
    }

    /**
     * BUYER: View own payments (authenticated).
     * GET /api/payments/buyer
     */
    @GetMapping("/buyer")
    public ResponseEntity<List<PaymentResponse>> buyerPayments(
            Authentication authentication
    ) {
        String buyerEmail = authentication.getName();
        List<PaymentResponse> payments = paymentService.getPaymentsForBuyer(buyerEmail);
        return ResponseEntity.ok(payments);
    }

    // -----------------------------------------------------------------
    // DIAGNOSTIC DEBUG ENDPOINTS (LOCAL-ONLY)
    // -----------------------------------------------------------------

    /**
     * Diagnostic endpoint: create payment for a hard-coded test buyer email and
     * return detailed error + stacktrace on failure.
     *
     * Usage (local only):
     * POST /api/payments/debug/create-diagnostics
     * Body: { "auctionId": 1, "paymentMethod": "UPI" }
     *
     * NOTE: Change testBuyerEmail below to an email that exists in your DB for testing.
     */
    @PostMapping("/debug/create-diagnostics")
    public ResponseEntity<?> debugCreateDiagnostics(@RequestBody PaymentCreateRequest req) {
        // IMPORTANT: replace this with a real email from your DB while testing.
        final String testBuyerEmail = "buyer@example.com"; // <-- CHANGE before calling

        try {
            PaymentResponse res = paymentService.createPayment(testBuyerEmail, req);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            // capture stacktrace
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            // return a detailed diagnostic response (HTTP 500)
            DiagnosticError diag = new DiagnosticError();
            diag.setError(ex.getClass().getName() + ": " + ex.getMessage());
            diag.setStacktrace(stackTrace);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(diag);
        }
    }

    /**
     * DEBUG: create payment as any email (no auth) - local testing only.
     * POST /api/payments/debug/create-as/{email}
     *
     * Example:
     * POST /api/payments/debug/create-as/buyer123@gmail.com
     * Body: { "auctionId": 2, "paymentMethod": "UPI" }
     *
     * NOTE: This endpoint bypasses auth — use only locally and remove after testing.
     */
    @PostMapping("/debug/create-as/{email}")
    public ResponseEntity<?> debugCreateAs(
            @PathVariable("email") String email,
            @RequestBody PaymentCreateRequest req
    ) {
        try {
            PaymentResponse res = paymentService.createPayment(email, req);
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            // return error + stack for quick debugging
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            Map<String, String> body = new HashMap<>();
            body.put("error", ex.getClass().getName() + ": " + ex.getMessage());
            body.put("stacktrace", sw.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // Simple POJO for diagnostic response
    static class DiagnosticError {
        private String error;
        private String stacktrace;

        public DiagnosticError() {}

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getStacktrace() { return stacktrace; }
        public void setStacktrace(String stacktrace) { this.stacktrace = stacktrace; }
    }
}

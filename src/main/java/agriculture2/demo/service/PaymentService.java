package agriculture2.demo.service;

import java.util.List;
import agriculture2.demo.dto.FarmerPayoutRequest;
import agriculture2.demo.dto.FarmerPayoutResponse;
import agriculture2.demo.dto.PaymentCreateRequest;
import agriculture2.demo.dto.PaymentResponse;

public interface PaymentService {

    // ---------- FARMER: DEFAULT PAYOUT ----------
    FarmerPayoutResponse getFarmerPayoutDetails(String farmerEmail);

    FarmerPayoutResponse updateFarmerPayoutDetails(
            String farmerEmail,
            FarmerPayoutRequest request
    );

    // ---------- BUYER: PAYMENTS ----------
    PaymentResponse createPayment(
            String buyerEmail,
            PaymentCreateRequest request
    );

    PaymentResponse markPaymentSuccess(
            Long paymentId,
            String gatewayPaymentId
    );

    // ---------- HISTORY ----------
    List<PaymentResponse> getPaymentsForBuyer(String buyerEmail);

    List<PaymentResponse> getPaymentsForFarmer(String farmerEmail);
}

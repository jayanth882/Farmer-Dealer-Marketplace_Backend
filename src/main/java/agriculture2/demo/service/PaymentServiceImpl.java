package agriculture2.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import agriculture2.demo.dto.FarmerPayoutRequest;
import agriculture2.demo.dto.FarmerPayoutResponse;
import agriculture2.demo.dto.PaymentCreateRequest;
import agriculture2.demo.dto.PaymentResponse;
import agriculture2.demo.entities.Auction;
import agriculture2.demo.entities.Payment;
import agriculture2.demo.entities.product;
import agriculture2.demo.entities.users;
import agriculture2.demo.repository.AuctionRepo;
import agriculture2.demo.repository.PaymentRepo;
import agriculture2.demo.repository.UserRepo;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final UserRepo userRepo;
    private final AuctionRepo auctionRepo;
    private final PaymentRepo paymentRepo;

    public PaymentServiceImpl(
            UserRepo userRepo,
            AuctionRepo auctionRepo,
            PaymentRepo paymentRepo
    ) {
        this.userRepo = userRepo;
        this.auctionRepo = auctionRepo;
        this.paymentRepo = paymentRepo;
    }

    // ================= FARMER PAYOUT =================

    @Override
    public FarmerPayoutResponse getFarmerPayoutDetails(String farmerEmail) {

        users farmer = userRepo.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        FarmerPayoutResponse res = new FarmerPayoutResponse();
        res.setFarmerEmail(farmer.getEmail());
        res.setPayoutMethod(farmer.getDefaultPayoutMethod().name());

        res.setUpiId(farmer.getDefaultUpiId());
        res.setAccountHolderName(farmer.getDefaultAccountHolderName());
        res.setAccountNumber(farmer.getDefaultAccountNumber());
        res.setIfscCode(farmer.getDefaultIfscCode());
        res.setBankName(farmer.getDefaultBankName());

        return res;
    }

    @Override
    public FarmerPayoutResponse updateFarmerPayoutDetails(
            String farmerEmail,
            FarmerPayoutRequest req
    ) {

        users farmer = userRepo.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        users.PayoutMethod method =
                users.PayoutMethod.valueOf(req.getPayoutMethod());

        farmer.setDefaultPayoutMethod(method);

        if (method == users.PayoutMethod.UPI) {
            farmer.setDefaultUpiId(req.getUpiId());
        }

        if (method == users.PayoutMethod.BANK) {
            farmer.setDefaultAccountHolderName(req.getAccountHolderName());
            farmer.setDefaultAccountNumber(req.getAccountNumber());
            farmer.setDefaultIfscCode(req.getIfscCode());
            farmer.setDefaultBankName(req.getBankName());
        }

        userRepo.save(farmer);
        return getFarmerPayoutDetails(farmerEmail);
    }

    // ================= BUYER PAYMENT =================

    @Override
    public PaymentResponse createPayment(
            String buyerEmail,
            PaymentCreateRequest request
    ) {

        users buyer = userRepo.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Auction auction = auctionRepo.findById(request.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // ✅ Only the winning buyer can pay
        if (auction.getWinningBuyer() == null ||
            !auction.getWinningBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("You are not allowed to pay for this auction");
        }

        // ✅ Prevent double payment
        if (!paymentRepo.findByAuction(auction).isEmpty()) {
            throw new RuntimeException("Payment already created for this auction");
        }

        product product = auction.getProduct();
        users farmer = product.getFarmer();

        Payment payment = new Payment();
        payment.setBuyer(buyer);
        payment.setFarmer(farmer);
        payment.setAuction(auction);
        payment.setAmount(auction.getFinalPrice());
        payment.setPaymentMethod(
                Payment.PaymentMethod.valueOf(request.getPaymentMethod())
        );
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return mapToResponse(paymentRepo.save(payment));
    }

    @Override
    public PaymentResponse markPaymentSuccess(
            Long paymentId,
            String gatewayPaymentId
    ) {

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setGatewayPaymentId(gatewayPaymentId);

        return mapToResponse(paymentRepo.save(payment));
    }

    // ================= HISTORY =================

    @Override
    public List<PaymentResponse> getPaymentsForBuyer(String buyerEmail) {

        users buyer = userRepo.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        return paymentRepo.findByBuyer(buyer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsForFarmer(String farmerEmail) {

        users farmer = userRepo.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return paymentRepo.findByFarmer(farmer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= MAPPER =================

    private PaymentResponse mapToResponse(Payment p) {

        PaymentResponse res = new PaymentResponse();

        res.setId(p.getId());
        res.setAuctionId(p.getAuction().getId());
        res.setAmount(p.getAmount());
        res.setStatus(p.getStatus().name());
        res.setPaymentMethod(
                p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null
        );

        res.setBuyerEmail(p.getBuyer().getEmail());
        res.setFarmerEmail(p.getFarmer().getEmail());
        res.setCreatedAt(p.getCreatedAt());

        return res;
    }
}

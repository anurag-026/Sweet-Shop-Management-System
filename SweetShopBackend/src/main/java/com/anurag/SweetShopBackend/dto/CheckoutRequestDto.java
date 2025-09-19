package com.anurag.SweetShopBackend.dto;

import com.anurag.SweetShopBackend.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDto {
    private Order.PaymentMode paymentMode;
    private PaymentDetailsDto paymentDetails;
    private String shippingAddress;
    private String customerNotes;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetailsDto {
        // Credit Card fields
        private String cardNumber;
        private String expiryDate;
        private String cvv;
        private String cardholderName;
        
        // PayPal fields
        private String paypalEmail;
        
        // Generic fields
        private String transactionId;
    }
}

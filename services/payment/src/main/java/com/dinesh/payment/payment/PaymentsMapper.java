package com.dinesh.payment.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentsMapper {
    public Payment toPayment(PaymentRequest request) {
        return Payment.builder()
                .paymentMethod(request.paymentMethod())
                .id(request.id())
                .amount(request.amount())
                .orderId(request.orderId())
                .build();
    }
}

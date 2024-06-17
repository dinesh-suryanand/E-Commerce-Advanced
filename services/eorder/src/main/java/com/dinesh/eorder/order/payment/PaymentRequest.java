package com.dinesh.eorder.order.payment;

import com.dinesh.eorder.customer.CustomerResponse;
import com.dinesh.eorder.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}

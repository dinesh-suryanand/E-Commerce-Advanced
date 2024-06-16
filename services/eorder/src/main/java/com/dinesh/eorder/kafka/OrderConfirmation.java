package com.dinesh.eorder.kafka;

import com.dinesh.eorder.customer.CustomerResponse;
import com.dinesh.eorder.order.PaymentMethod;
import com.dinesh.eorder.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}

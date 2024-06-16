package com.dinesh.eorder.product;

import java.math.BigDecimal;

public record PurchaseResponse(
        Integer productId,
        String name,
        String description,
        BigDecimal price,
        Double quantity
) {
}

package com.nexbill.billing_service.domain.valueobject;

import java.math.BigDecimal;

public record ExpenditureInput(
        String name,
        String description,
        BigDecimal price
) {
}

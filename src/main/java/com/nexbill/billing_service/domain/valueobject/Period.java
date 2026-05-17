package com.nexbill.billing_service.domain.valueobject;

import java.time.YearMonth;

public class Period {

    private final YearMonth value;

    public Period(YearMonth value) {

        if (value == null) {
            throw new IllegalArgumentException("Billing period is required");
        }

        this.value = value;
    }

    public YearMonth getValue() {
        return value;
    }
}

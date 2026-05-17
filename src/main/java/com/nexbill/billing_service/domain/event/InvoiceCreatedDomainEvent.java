package com.nexbill.billing_service.domain.event;

import com.nexbill.billing_service.domain.enums.InvoiceSource;
import com.nexbill.billing_service.domain.enums.InvoiceType;

import java.time.YearMonth;
import java.util.UUID;

public record InvoiceCreatedDomainEvent(
        UUID invoicePublicId,
        InvoiceType type,
        InvoiceSource source,
        YearMonth period
) {
}

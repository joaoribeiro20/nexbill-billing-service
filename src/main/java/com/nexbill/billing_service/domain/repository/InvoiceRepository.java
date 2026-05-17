package com.nexbill.billing_service.domain.repository;

import com.nexbill.billing_service.domain.aggregate.Invoice;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);
}

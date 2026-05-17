package com.nexbill.billing_service.application.event.processor;

import com.nexbill.billing_service.domain.event.InvoiceCreatedDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceERPProcessor {

    private static final Logger log = LoggerFactory.getLogger(InvoiceERPProcessor.class);

    @EventListener
    public void onInvoiceCreated(InvoiceCreatedDomainEvent event) {
        log.info("Invoice created event received: publicId={}", event.invoicePublicId());
    }
}

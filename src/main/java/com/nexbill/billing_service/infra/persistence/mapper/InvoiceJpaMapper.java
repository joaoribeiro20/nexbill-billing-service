package com.nexbill.billing_service.infra.persistence.mapper;

import com.nexbill.billing_service.domain.aggregate.Invoice;
import com.nexbill.billing_service.infra.persistence.entity.InvoiceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoiceJpaMapper {

    public InvoiceJpaEntity toJpaEntity(Invoice domain) {

        InvoiceJpaEntity entity = new InvoiceJpaEntity();

        entity.setPublicId(domain.getPublicId());
        entity.setType(domain.getType());
        entity.setSource(domain.getSource());
        entity.setPeriod(domain.getPeriod().getValue());
        entity.setStatus(domain.getStatus());
        entity.setTotalAmount(domain.getTotalAmount().getValue());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setOccurredAt(domain.getOccurredAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}

package com.nexbill.billing_service.infra.persistence.mapper;

import com.nexbill.billing_service.domain.entity.Expenditure;
import com.nexbill.billing_service.infra.persistence.entity.ExpenditureJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExpenditureJpaMapper {

    public ExpenditureJpaEntity toJpaEntity(Expenditure domain) {

        ExpenditureJpaEntity entity = new ExpenditureJpaEntity();

        entity.setPublicId(domain.getPublicId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setAmount(domain.getAmount().getValue());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setOccurredAt(domain.getOccurredAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}

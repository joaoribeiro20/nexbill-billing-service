package com.nexbill.billing_service.infra.persistence.repository;

import com.nexbill.billing_service.infra.persistence.entity.InvoiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, Long> {
}

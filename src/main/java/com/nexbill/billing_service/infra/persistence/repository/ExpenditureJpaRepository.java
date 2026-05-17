package com.nexbill.billing_service.infra.persistence.repository;

import com.nexbill.billing_service.infra.persistence.entity.ExpenditureJpaEntity;
import com.nexbill.billing_service.infra.persistence.entity.InvoiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenditureJpaRepository extends JpaRepository<ExpenditureJpaEntity, Long> {

    @Modifying
    @Query("UPDATE ExpenditureJpaEntity e SET e.invoice = :invoice WHERE e.id IN :ids")
    void linkToInvoice(@Param("invoice") InvoiceJpaEntity invoice, @Param("ids") List<Long> ids);
}

package com.nexbill.billing_service.infra.persistence.adapter;

import com.nexbill.billing_service.domain.aggregate.Invoice;
import com.nexbill.billing_service.domain.entity.Expenditure;
import com.nexbill.billing_service.domain.repository.InvoiceRepository;
import com.nexbill.billing_service.infra.persistence.entity.InvoiceJpaEntity;
import com.nexbill.billing_service.infra.persistence.mapper.InvoiceJpaMapper;
import com.nexbill.billing_service.infra.persistence.repository.ExpenditureJpaRepository;
import com.nexbill.billing_service.infra.persistence.repository.InvoiceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository invoiceJpaRepository;
    private final ExpenditureJpaRepository expenditureJpaRepository;
    private final InvoiceJpaMapper mapper;

    public InvoiceRepositoryAdapter(
            InvoiceJpaRepository invoiceJpaRepository,
            ExpenditureJpaRepository expenditureJpaRepository,
            InvoiceJpaMapper mapper
    ) {
        this.invoiceJpaRepository = invoiceJpaRepository;
        this.expenditureJpaRepository = expenditureJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Invoice save(Invoice invoice) {

        if (!invoice.isReadyToPersist()) {
            throw new IllegalStateException("Invoice is not ready to persist");
        }

        InvoiceJpaEntity invoiceEntity = mapper.toJpaEntity(invoice);
        InvoiceJpaEntity savedInvoice = invoiceJpaRepository.save(invoiceEntity);

        List<Long> expenditureIds = invoice.getExpenditures().stream()
                .map(Expenditure::getId)
                .toList();

        expenditureJpaRepository.linkToInvoice(savedInvoice, expenditureIds);

        invoice.assignPersistedId(savedInvoice.getId());

        return invoice;
    }
}

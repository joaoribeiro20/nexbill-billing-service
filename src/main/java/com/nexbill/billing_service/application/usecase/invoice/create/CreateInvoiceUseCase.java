package com.nexbill.billing_service.application.usecase.invoice.create;

import com.nexbill.billing_service.application.port.event.DomainEventPublisher;
import com.nexbill.billing_service.domain.aggregate.Invoice;
import com.nexbill.billing_service.domain.entity.Expenditure;
import com.nexbill.billing_service.domain.repository.ExpenditureRepository;
import com.nexbill.billing_service.domain.repository.InvoiceRepository;
import com.nexbill.billing_service.domain.valueobject.ExpenditureInput;
import com.nexbill.billing_service.domain.valueobject.Period;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class CreateInvoiceUseCase {

    /*
     * =========================================================
     * DEPENDENCIES
     * =========================================================
     */

    private final InvoiceRepository invoiceRepository;
    private final ExpenditureRepository expenditureRepository;
    private final DomainEventPublisher publisher;

    public CreateInvoiceUseCase(
            InvoiceRepository invoiceRepository,
            ExpenditureRepository expenditureRepository,
            DomainEventPublisher publisher
    ) {
        this.invoiceRepository = invoiceRepository;
        this.expenditureRepository = expenditureRepository;
        this.publisher = publisher;
    }

    /*
     * =========================================================
     * USE CASE
     * =========================================================
     */

    @Transactional
    public void execute(CreateInvoiceRequest request) {

        /*
         * Resolve the actor responsible for the invoice creation.
         * If no user is provided, fallback to "system".
         */
        String createdBy = resolveCreatedBy(
                request.createdBy()
        );

        /*
         * Convert request DTOs into domain value objects.
         * The domain layer should work with domain objects,
         * not transport/request objects.
         */
        List<ExpenditureInput> expenditureInputs = request.expenditures()
                .stream()
                .map(exp -> new ExpenditureInput(
                        exp.name(),
                        exp.description(),
                        exp.price()
                ))
                .toList();

        /*
         * Create the invoice aggregate.
         *
         * At this point:
         * - invoice is initialized
         * - expenditures are registered
         * - total amount is calculated
         *
         * Nothing is persisted yet.
         */
        Invoice invoice = Invoice.create(
                request.type(),
                request.source(),
                new Period(
                        YearMonth.parse(request.period())
                ),
                expenditureInputs,
                createdBy
        );

        /*
         * Persist all expenditures first.
         *
         * Expenditures must exist in the database before
         * the invoice can move to PENDING state.
         */
        List<Expenditure> persistedExpenditures =
                expenditureRepository.saveAll(
                        invoice.getExpenditures()
                );

        /*
         * Replace temporary expenditures with persisted ones.
         *
         * Persisted expenditures now contain:
         * - database ids
         * - timestamps
         * - persistence state
         */
        invoice.replacePersistedExpenditures(
                persistedExpenditures
        );

        /*
         * Finalize the invoice creation flow.
         *
         * This validates:
         * - all expenditures are persisted
         * - invoice can move to PENDING state
         *
         * Also registers domain events.
         */
        invoice.markAsPending();

        /*
         * Persist the invoice aggregate.
         */
        invoiceRepository.save(invoice);

        /*
         * Publish all domain events generated during
         * the invoice lifecycle.
         */
        publisher.publish(
                invoice.getDomainEvents()
        );

        /*
         * Clear events after publishing to avoid
         * duplicated event dispatching.
         */
        invoice.clearDomainEvents();
    }

    /*
     * =========================================================
     * HELPERS
     * =========================================================
     */

    private String resolveCreatedBy(String createdBy) {

        return createdBy != null && !createdBy.isBlank()
                ? createdBy
                : "system";
    }
}
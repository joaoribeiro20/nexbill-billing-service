package com.nexbill.billing_service.domain.aggregate;

import com.nexbill.billing_service.domain.entity.Expenditure;
import com.nexbill.billing_service.domain.enums.InvoiceSource;
import com.nexbill.billing_service.domain.enums.InvoiceStatus;
import com.nexbill.billing_service.domain.enums.InvoiceType;
import com.nexbill.billing_service.domain.event.InvoiceCreatedDomainEvent;
import com.nexbill.billing_service.domain.valueobject.ExpenditureInput;
import com.nexbill.billing_service.domain.valueobject.Money;
import com.nexbill.billing_service.domain.valueobject.Period;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Invoice {

    /*
     * =========================================================
     * INTERNAL ENUMS
     * =========================================================
     */

    private enum CreationPhase {
        EXPENDITURES_REGISTERED,
        EXPENDITURES_PERSISTED,
        COMPLETED
    }

    /*
     * =========================================================
     * ENTITY FIELDS
     * =========================================================
     */

    private Long id;
    private UUID publicId;

    private InvoiceType type;
    private InvoiceSource source;
    private InvoiceStatus status;

    private Period period;

    private List<Expenditure> expenditures;

    private Money totalAmount;

    private String createdBy;
    private String updatedBy;

    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CreationPhase creationPhase;

    private final List<Object> domainEvents = new ArrayList<>();

    /*
     * =========================================================
     * CONSTRUCTORS
     * =========================================================
     */

    private Invoice(
            InvoiceType type,
            InvoiceSource source,
            Period period,
            String createdBy
    ) {

        validateType(type);
        validateSource(source);
        validatePeriod(period);

        this.publicId = UUID.randomUUID();

        this.type = type;
        this.source = source;
        this.period = period;

        this.createdBy = createdBy;
        this.updatedBy = createdBy;

        this.expenditures = new ArrayList<>();

        this.totalAmount = Money.zero();

        this.occurredAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /*
     * =========================================================
     * FACTORY METHODS
     * =========================================================
     */

    public static Invoice create(
            InvoiceType type,
            InvoiceSource source,
            Period period,
            List<ExpenditureInput> inputs,
            String createdBy
    ) {

        String actor = resolveActor(createdBy);

        Invoice invoice = new Invoice(
                type,
                source,
                period,
                actor
        );

        invoice.registerExpenditures(inputs, actor);

        return invoice;
    }

    /*
     * =========================================================
     * PUBLIC BUSINESS METHODS
     * =========================================================
     */

    public void assignPersistedId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Invoice id is required");
        }

        this.id = id;
    }

    public void replacePersistedExpenditures(
            List<Expenditure> persistedExpenditures
    ) {

        ensurePhase(
                CreationPhase.EXPENDITURES_REGISTERED,
                "Expenditures must be registered before replacing persisted expenditures"
        );

        if (persistedExpenditures == null) {
            throw new IllegalArgumentException(
                    "Persisted expenditures are required"
            );
        }

        if (persistedExpenditures.size() != expenditures.size()) {
            throw new IllegalStateException(
                    "Persisted expenditures count does not match registered expenditures"
            );
        }

        expenditures.clear();
        expenditures.addAll(persistedExpenditures);

        creationPhase = CreationPhase.EXPENDITURES_PERSISTED;
    }

    public void markAsPending() {

        ensurePhase(
                CreationPhase.EXPENDITURES_PERSISTED,
                "Expenditures must be persisted before marking invoice as pending"
        );

        boolean hasUnpersisted = expenditures.stream()
                .anyMatch(expenditure -> expenditure.getId() == null);

        if (hasUnpersisted) {
            throw new IllegalStateException(
                    "All expenditures must be persisted before marking invoice as pending"
            );
        }

        this.status = InvoiceStatus.PENDING;
        this.creationPhase = CreationPhase.COMPLETED;
        this.updatedAt = LocalDateTime.now();

        registerEvent(
                new InvoiceCreatedDomainEvent(
                        publicId,
                        type,
                        source,
                        period.getValue()
                )
        );
    }

    public boolean isReadyToPersist() {
        return creationPhase == CreationPhase.COMPLETED
                && status == InvoiceStatus.PENDING;
    }

    /*
     * =========================================================
     * CREATION FLOW
     * =========================================================
     */

    private void registerExpenditures(
            List<ExpenditureInput> inputs,
            String createdBy
    ) {

        validateExpenditures(inputs);

        expenditures.addAll(
                Expenditure.createAll(inputs, createdBy)
        );

        creationPhase = CreationPhase.EXPENDITURES_REGISTERED;

        recalculateTotal();
    }

    /*
     * =========================================================
     * BUSINESS RULES
     * =========================================================
     */

    private void recalculateTotal() {

        Money total = Money.zero();

        for (Expenditure expenditure : expenditures) {
            total = total.add(expenditure.getAmount());
        }

        totalAmount = total;
    }

    private void ensurePhase(
            CreationPhase expected,
            String message
    ) {

        if (creationPhase != expected) {
            throw new IllegalStateException(message);
        }
    }

    /*
     * =========================================================
     * DOMAIN EVENTS
     * =========================================================
     */

    private void registerEvent(Object event) {
        domainEvents.add(event);
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /*
     * =========================================================
     * VALIDATIONS
     * =========================================================
     */

    private void validateType(InvoiceType type) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Invoice type is required"
            );
        }
    }

    private void validateSource(InvoiceSource source) {

        if (source == null) {
            throw new IllegalArgumentException(
                    "Invoice source is required"
            );
        }
    }

    private void validatePeriod(Period period) {

        if (period == null) {
            throw new IllegalArgumentException(
                    "Billing period is required"
            );
        }
    }

    private void validateExpenditures(
            List<ExpenditureInput> inputs
    ) {

        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one expenditure is required"
            );
        }
    }

    /*
     * =========================================================
     * HELPERS
     * =========================================================
     */

    private static String resolveActor(String createdBy) {

        return createdBy != null && !createdBy.isBlank()
                ? createdBy
                : "system";
    }

    /*
     * =========================================================
     * GETTERS
     * =========================================================
     */

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public InvoiceType getType() {
        return type;
    }

    public InvoiceSource getSource() {
        return source;
    }

    public Period getPeriod() {
        return period;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public List<Expenditure> getExpenditures() {
        return Collections.unmodifiableList(expenditures);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
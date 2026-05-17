package com.nexbill.billing_service.domain.entity;

import com.nexbill.billing_service.domain.enums.ExpenditureStatus;
import com.nexbill.billing_service.domain.valueobject.ExpenditureInput;
import com.nexbill.billing_service.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Expenditure {

    private Long id;
    private UUID publicId;
    private String name;
    private String description;
    private Money amount;
    private ExpenditureStatus status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Expenditure(
            String name,
            String description,
            Money amount,
            String createdBy
    ) {
        validateName(name);
        validateDescription(description);
        validateAmount(amount);

        this.publicId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.status = ExpenditureStatus.CREATED;
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
        this.occurredAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Expenditure create(ExpenditureInput input, String createdBy) {
        return new Expenditure(
                input.name(),
                input.description(),
                new Money(input.price()),
                createdBy
        );
    }

    public static List<Expenditure> createAll(List<ExpenditureInput> inputs, String createdBy) {

        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("At least one expenditure is required");
        }

        List<Expenditure> expenditures = new ArrayList<>();

        for (ExpenditureInput input : inputs) {
            expenditures.add(create(input, createdBy));
        }

        return expenditures;
    }

    public void assignPersistedId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Expenditure id is required");
        }

        this.id = id;
    }

    private void validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Expenditure name is required");
        }
    }

    private void validateDescription(String description) {

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Expenditure description is required");
        }
    }

    private void validateAmount(Money amount) {

        if (amount == null) {
            throw new IllegalArgumentException("Expenditure amount is required");
        }

        if (amount.isZeroOrNegative()) {
            throw new IllegalArgumentException("Expenditure amount must be greater than zero");
        }
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getAmount() {
        return amount;
    }

    public ExpenditureStatus getStatus() {
        return status;
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

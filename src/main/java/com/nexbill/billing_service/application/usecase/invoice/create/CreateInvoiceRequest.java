package com.nexbill.billing_service.application.usecase.invoice.create;

import com.nexbill.billing_service.domain.enums.InvoiceSource;
import com.nexbill.billing_service.domain.enums.InvoiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CreateInvoiceRequest(
        @NotNull InvoiceType type,
        @NotNull InvoiceSource source,
        @NotBlank
        @Pattern(regexp = "\\d{4}-\\d{2}", message = "period must be in format YYYY-MM")
        String period,
        @NotEmpty @Valid List<ExpenditureRequest> expenditures,
        String createdBy
) {
}

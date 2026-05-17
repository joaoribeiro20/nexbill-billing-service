package com.nexbill.billing_service.interfaces.rest.invoice;

import com.nexbill.billing_service.application.usecase.invoice.create.CreateInvoiceRequest;
import com.nexbill.billing_service.application.usecase.invoice.create.CreateInvoiceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/invoice")
public class InvoiceController {

    private final CreateInvoiceUseCase useCase;

    public InvoiceController(CreateInvoiceUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateInvoiceRequest request) {
        useCase.execute(request);
        return ResponseEntity.accepted().build();
    }
}

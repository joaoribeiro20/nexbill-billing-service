package com.nexbill.billing_service.application.event.publisher;

import com.nexbill.billing_service.application.port.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternalEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public InternalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(List<Object> events) {
        events.forEach(applicationEventPublisher::publishEvent);
    }
}

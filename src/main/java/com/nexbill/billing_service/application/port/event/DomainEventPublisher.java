package com.nexbill.billing_service.application.port.event;

import java.util.List;

public interface DomainEventPublisher {

    void publish(List<Object> events);
}

package com.nexbill.billing_service.domain.repository;

import com.nexbill.billing_service.domain.entity.Expenditure;

import java.util.List;

public interface ExpenditureRepository {

    List<Expenditure> saveAll(List<Expenditure> expenditures);
}

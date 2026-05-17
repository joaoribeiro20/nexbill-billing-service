package com.nexbill.billing_service.infra.persistence.adapter;

import com.nexbill.billing_service.domain.entity.Expenditure;
import com.nexbill.billing_service.domain.repository.ExpenditureRepository;
import com.nexbill.billing_service.infra.persistence.entity.ExpenditureJpaEntity;
import com.nexbill.billing_service.infra.persistence.mapper.ExpenditureJpaMapper;
import com.nexbill.billing_service.infra.persistence.repository.ExpenditureJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ExpenditureRepositoryAdapter implements ExpenditureRepository {

    private final ExpenditureJpaRepository jpaRepository;
    private final ExpenditureJpaMapper mapper;

    public ExpenditureRepositoryAdapter(ExpenditureJpaRepository jpaRepository, ExpenditureJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Expenditure> saveAll(List<Expenditure> expenditures) {

        List<ExpenditureJpaEntity> entities = expenditures.stream()
                .map(mapper::toJpaEntity)
                .toList();

        List<ExpenditureJpaEntity> savedEntities = jpaRepository.saveAll(entities);

        List<Expenditure> persisted = new ArrayList<>();

        for (int index = 0; index < expenditures.size(); index++) {
            Expenditure expenditure = expenditures.get(index);
            expenditure.assignPersistedId(savedEntities.get(index).getId());
            persisted.add(expenditure);
        }

        return persisted;
    }
}

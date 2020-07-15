package com.twino.homework.db.repository;

import com.twino.homework.db.entity.LoanEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LoanRepository extends PagingAndSortingRepository<LoanEntity, Integer> {
}

package com.twino.homework.db.repository;

import com.twino.homework.db.entity.LoanEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

@RepositoryRestResource(path = "loan")
public interface LoanRepository extends PagingAndSortingRepository<LoanEntity, Integer> {
    Collection<LoanEntity> findByUserByUserId_UniqueId(@Param("uuid") String uuid);
}

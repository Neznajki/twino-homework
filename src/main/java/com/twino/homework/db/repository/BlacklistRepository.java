package com.twino.homework.db.repository;

import com.twino.homework.db.entity.BlacklistEntity;
import com.twino.homework.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "blacklist")
public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Integer> {
    BlacklistEntity findByUserByUserId(UserEntity user);
}

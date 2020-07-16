package com.twino.homework.db.repository;


import com.twino.homework.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "user")
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByNameAndSurname(@Param("name") String name, @Param("surname") String surname);
    UserEntity findByUniqueId(@Param("uniqueId") String uniqueId);
}

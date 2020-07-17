package com.twino.homework.service;

import com.twino.homework.db.entity.BlacklistEntity;
import com.twino.homework.db.entity.UserEntity;
import com.twino.homework.db.repository.BlacklistRepository;
import com.twino.homework.db.repository.UserRepository;
import com.twino.homework.exception.UserNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl {
    UserRepository userRepository;
    BlacklistRepository blacklistRepository;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, BlacklistRepository blacklistRepository) {
        this.userRepository = userRepository;
        this.blacklistRepository = blacklistRepository;
    }

    public UserEntity addToBlacklistByUuid(String uuid) throws UserNotFoundException {
        UserEntity user = this.userRepository.findByUniqueId(uuid);

        if (user == null) {
            throw new UserNotFoundException(uuid);
        }

        BlacklistEntity blacklistEntity = new BlacklistEntity();

        blacklistEntity.setUserByUserId(user);

        try {
            blacklistRepository.save(blacklistEntity);
        } catch (ConstraintViolationException duplicateEntryException) {
            logger.warn(String.format("duplicate key on adding user %s", uuid));
        }

        user.setBlacklistsById(blacklistEntity);

        return user;
    }

    public UserEntity getUserEntity(String name, String surname) {
        UserEntity user = this.userRepository.findByNameAndSurname(name, surname);

        if (user != null) {
            return user;
        }

        user = new UserEntity();

        user.setName(name);
        user.setSurname(surname);
        user.setUniqueId(UUID.randomUUID().toString());
        user.setCreated(new Timestamp(System.currentTimeMillis()));

        try {
            this.userRepository.save(user);
        } catch (ConstraintViolationException e) {
            return getUserAfterDuplicateEntry(name, surname);
        }

        return user;
    }

    public Boolean isUserBlacklisted(UserEntity user) {
        return this.blacklistRepository.findByUserByUserId(user) != null;
    }

    protected UserEntity getUserAfterDuplicateEntry(String name, String surname) {
        UserEntity user;
        user = this.userRepository.findByNameAndSurname(name, surname);

        if (user == null) {
            user = retryWithPause(name, surname);
        }

        return user;
    }

    protected UserEntity retryWithPause(String name, String surname) {
        try {
            pause(10);
        } catch (InterruptedException e) {
            logger.warn("interrupted sleep timeout on repeat user search delay", e);
        }

        UserEntity user = this.userRepository.findByNameAndSurname(name, surname);

        if (user == null) {
            throw new RuntimeException(String.format("could not create user %s, %s could be master slave problem", name, surname));
        }

        return user;
    }

    protected void pause(int timeoutSeconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(timeoutSeconds);
    }
}

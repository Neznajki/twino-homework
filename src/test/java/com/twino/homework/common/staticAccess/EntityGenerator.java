package com.twino.homework.common.staticAccess;

import com.twino.homework.db.entity.LoanEntity;
import com.twino.homework.db.entity.UserEntity;

import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;

public class EntityGenerator {
    public static UserEntity getTestUserEntity() {
        UserEntity userEntity = new UserEntity();

        userEntity.setId(ThreadLocalRandom.current().nextInt(10000, 20000));
        userEntity.setUniqueId("UNUT TEST UUID");
        userEntity.setName("UNUT TEST name");
        userEntity.setSurname("UNUT TEST surname");
        userEntity.setCreated(new Timestamp(System.currentTimeMillis()));
        return userEntity;
    }

    public static LoanEntity getTestLoanEntity() {
        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setId(ThreadLocalRandom.current().nextInt(0, 5000));
        loanEntity.setTermDays(ThreadLocalRandom.current().nextInt(5000, 10000));
        loanEntity.setCountryIsoCode("TEST");
        loanEntity.setAmount(20.0);
        loanEntity.setCreated(new Timestamp(System.currentTimeMillis()));

        UserEntity userEntity = getTestUserEntity();

        loanEntity.setUserByUserId(userEntity);

        return loanEntity;
    }
}

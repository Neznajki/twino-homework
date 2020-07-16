package com.twino.homework.web.response;

import com.twino.homework.db.entity.UserEntity;

import java.sql.Timestamp;

public class UserResponseData {
    private String uniqueId;
    private String name;
    private String surname;
    private Timestamp created;

    public UserResponseData(UserEntity userEntity) {
        uniqueId = userEntity.getUniqueId();
        name = userEntity.getName();
        surname = userEntity.getSurname();
        created = userEntity.getCreated();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Timestamp getCreated() {
        return created;
    }
}

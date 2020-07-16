package com.twino.homework.db.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "blacklist", schema = "loans")
public class BlacklistEntity {
    private int id;
    private UserEntity userByUserId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlacklistEntity that = (BlacklistEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }
}

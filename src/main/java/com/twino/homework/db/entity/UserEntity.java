package com.twino.homework.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "loans")
public class UserEntity {
    private int id;
    private String uniqueId;
    private String name;
    private String surname;
    private Timestamp created;
    private Collection<LoanEntity> loansById;
    private BlacklistEntity blacklistsById;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "unique_id")
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Basic
    @Column(name = "created")
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id == that.id &&
                Objects.equals(uniqueId, that.uniqueId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uniqueId, name, surname, created);
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<LoanEntity> getLoansById() {
        return loansById;
    }

    public void setLoansById(Collection<LoanEntity> loansById) {
        this.loansById = loansById;
    }

    @OneToOne(mappedBy = "userByUserId")
    public BlacklistEntity getBlacklistsById() {
        return blacklistsById;
    }

    public void setBlacklistsById(BlacklistEntity blacklistsById) {
        this.blacklistsById = blacklistsById;
    }
}

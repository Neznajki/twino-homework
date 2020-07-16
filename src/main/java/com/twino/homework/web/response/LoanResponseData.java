package com.twino.homework.web.response;

import com.twino.homework.db.entity.LoanEntity;

import java.sql.Timestamp;

/** could be reworked to http://localhost:8080/loan/1 **/
public class LoanResponseData {
    private int id;
    private double amount;
    private int termDays;
    private Timestamp created;
    private String countryIsoCode;
    private UserResponseData user;

    public LoanResponseData(LoanEntity loanEntity) {
        id = loanEntity.getId();
        amount = loanEntity.getAmount();
        termDays = loanEntity.getTermDays();
        created = loanEntity.getCreated();
        countryIsoCode = loanEntity.getCountryIsoCode();
        user = new UserResponseData(loanEntity.getUserByUserId());
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public int getTermDays() {
        return termDays;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public UserResponseData getUser() {
        return user;
    }
}

package com.twino.homework.web.request;

public class AddLoanRequest {
    Float loanAmount;
    Integer termDays;
    String name;
    String surname;

    public AddLoanRequest(Float loanAmount, Integer term, String name, String surname) {
        this.loanAmount = loanAmount;
        this.termDays = term;
        this.name = name.trim();
        this.surname = surname.trim();
    }

    public Float getLoanAmount() {
        return loanAmount;
    }

    public Integer getTermDays() {
        return termDays;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}

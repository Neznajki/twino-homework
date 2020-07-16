package com.twino.homework.web.request;

import java.util.Objects;

public class AddLoanRequest {
    Float loanAmount;
    Integer termDays;
    String name;
    String surname;

    public AddLoanRequest(Float loanAmount, Integer term, String name, String surname) {
        this.loanAmount = loanAmount;
        this.termDays = term;

        if (name != null) {
            this.name = name.trim();
        }
        if (surname != null) {
           this.surname = surname.trim();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddLoanRequest request = (AddLoanRequest) o;
        return Objects.equals(loanAmount, request.loanAmount) &&
            Objects.equals(termDays, request.termDays) &&
            Objects.equals(name, request.name) &&
            Objects.equals(surname, request.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanAmount, termDays, name, surname);
    }
}

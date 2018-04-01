package by.tareltos.fcqdelivery.specification.impl;

import by.tareltos.fcqdelivery.specification.SqlSpecification;

/**
 * Task 1 Chapter A
 * Created by Vitali Tarelko on 26.03.2018.
 * tareltos@gmail.com; skype: tareltos
 */
public class UserByEmailSpecification implements SqlSpecification {

    private String query = "SELECT email, password, role, firstName, lastName, phone FROM user WHERE email = \"%s\" ";
    private String email;

    public UserByEmailSpecification(String email) {
        this.email = email;
    }

    @Override
    public String toSqlClauses() {
        return String.format(query, email);
    }
}
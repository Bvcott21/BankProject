package com.bvcott.bubank.model;

import com.bvcott.bubank.model.account.Account;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "CUSTOMER_USER")
public class Customer {
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setUser(this);
    }

    public void removeAccount(Account account) {
        if (accounts.contains(account)) {
            accounts.remove(account);
            account.setUser(null);
        }
    }
}

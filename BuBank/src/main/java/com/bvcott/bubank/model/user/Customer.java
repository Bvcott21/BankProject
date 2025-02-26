package com.bvcott.bubank.model.user;

import com.bvcott.bubank.model.account.Account;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "CUSTOMER_USER") @Data @EqualsAndHashCode(callSuper = true)
public class Customer extends User {
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Account> accounts = new ArrayList<>();

    public Customer(String username, String password, Role role) {
        super(username, password, role);
    }
    
    public Customer() {}

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setCustomer(this);
    }

    public void removeAccount(Account account) {
        if (accounts.contains(account)) {
            accounts.remove(account);
            account.setCustomer(null);
        }
    }
}

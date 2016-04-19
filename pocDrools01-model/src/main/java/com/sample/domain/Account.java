package com.sample.domain;

public class Account {

    private Integer balance = Integer.valueOf(0);

    public Account() {
    }
    
    public Account(Integer balance) {
        super();
        this.balance = balance;
    }

    /**
     * @return the balance
     */
    public Integer getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(Integer balance) {
        this.balance = balance;
    }
    
    public void withdraw(int money) {
        balance = Integer.valueOf(balance.intValue() - money);
    }
    
}

package com.ibrhmdurna.chatapp.models;

public class Friend {

    private Long time;
    private Account account;

    public Friend() {
    }

    public Friend(Long time, Account account) {
        this.time = time;
        this.account = account;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}

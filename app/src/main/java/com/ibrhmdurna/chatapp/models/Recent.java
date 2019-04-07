package com.ibrhmdurna.chatapp.models;

public class Recent {

    private Long time;

    private Account account;

    public Recent() {
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

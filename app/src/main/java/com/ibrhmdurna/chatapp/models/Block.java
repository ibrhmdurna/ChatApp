package com.ibrhmdurna.chatapp.models;

public class Block {

    private Long time;
    private Account account;

    public Block() {
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

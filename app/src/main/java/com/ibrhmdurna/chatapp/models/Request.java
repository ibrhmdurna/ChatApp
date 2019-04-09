package com.ibrhmdurna.chatapp.models;

public class Request {

    private boolean seen;
    private Long time;

    private Account account;

    public Request() {
    }

    public Request(boolean seen, Long time) {
        this.seen = seen;
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
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

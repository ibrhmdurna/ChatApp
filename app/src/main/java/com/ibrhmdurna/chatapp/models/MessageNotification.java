package com.ibrhmdurna.chatapp.models;

public class MessageNotification {

    private String message;
    private long current_time;
    private String sender;

    public MessageNotification() {
    }

    public MessageNotification(String message, long current_time, String sender) {
        this.message = message;
        this.current_time = current_time;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(long current_time) {
        this.current_time = current_time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

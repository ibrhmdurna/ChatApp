package com.ibrhmdurna.chatapp.models;

public class Message {

    private String from;
    private String message;
    private String url;
    private String type;
    private Long time;
    private boolean send;
    private boolean seen;
    private boolean receive;

    private boolean profileVisibility;

    private String message_id;

    public Message() {
    }

    public Message(String from, String message, String url, String type, Long time, boolean send, boolean seen, boolean receive) {
        this.from = from;
        this.message = message;
        this.url = url;
        this.type = type;
        this.time = time;
        this.send = send;
        this.seen = seen;
        this.receive = receive;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isReceive() {
        return receive;
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

    public boolean isProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(boolean profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}

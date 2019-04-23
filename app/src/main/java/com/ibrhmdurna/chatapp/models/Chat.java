package com.ibrhmdurna.chatapp.models;

public class Chat {

    private String last_message_id;
    private String last_message;
    private Long time;
    private boolean seen;
    private boolean typing;

    private String chatUid;

    public Chat() {
    }

    public Chat(String last_message_id, String last_message, Long time, boolean seen, boolean typing) {
        this.last_message_id = last_message_id;
        this.last_message = last_message;
        this.time = time;
        this.seen = seen;
        this.typing = typing;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }

    public String getChatUid() {
        return chatUid;
    }

    public void setChatUid(String chatUid) {
        this.chatUid = chatUid;
    }
}

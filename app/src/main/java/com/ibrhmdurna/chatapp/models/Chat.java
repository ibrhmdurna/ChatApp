package com.ibrhmdurna.chatapp.models;

public class Chat {

    private Long time;
    private boolean seen;
    private boolean typing;

    private String chatUid;

    public Chat() {
    }

    public Chat(Long time, boolean seen, boolean typing) {
        this.time = time;
        this.seen = seen;
        this.typing = typing;
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

    public String getChatUid() {
        return chatUid;
    }

    public void setChatUid(String chatUid) {
        this.chatUid = chatUid;
    }
}

package com.ibrhmdurna.chatapp.database.strategy;

import com.ibrhmdurna.chatapp.models.Message;

public class SendMessage {

    private MessageStrategy strategy;
    private Message message;
    private String chatUid;

    public SendMessage(MessageStrategy strategy){
        this.strategy = strategy;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getChatUid() {
        return chatUid;
    }

    public void setChatUid(String chatUid) {
        this.chatUid = chatUid;
    }

    public void Send(){
        strategy.Send(getMessage(), getChatUid());
    }
}

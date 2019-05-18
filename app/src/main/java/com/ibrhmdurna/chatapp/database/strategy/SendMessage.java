package com.ibrhmdurna.chatapp.database.strategy;

import com.ibrhmdurna.chatapp.models.Message;

public class SendMessage {

    private MessageStrategy strategy;

    public SendMessage(MessageStrategy strategy){
        this.strategy = strategy;
    }

    public void send(Message message, String chatUid){
        strategy.send(message, chatUid);
    }
}

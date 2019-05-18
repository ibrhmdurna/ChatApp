package com.ibrhmdurna.chatapp.database.strategy;

import com.ibrhmdurna.chatapp.models.Message;

public interface MessageStrategy {
    void send(Message message, String chatUid);
}

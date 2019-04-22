package com.ibrhmdurna.chatapp.database.strategy;

import com.ibrhmdurna.chatapp.models.Message;

public abstract class MessageStrategy {
    public abstract void Send(Message message, String chatUid);
}

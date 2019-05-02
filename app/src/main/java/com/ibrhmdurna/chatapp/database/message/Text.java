package com.ibrhmdurna.chatapp.database.message;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.database.strategy.MessageStrategy;
import com.ibrhmdurna.chatapp.models.Message;

import java.util.HashMap;
import java.util.Map;

public class Text extends MessageStrategy {

    @Override
    public void Send(final Message message, final String chatUid) {

        final String message_id = Firebase.getInstance().getDatabaseReference().child("Messages").child(message.getFrom()).child(chatUid).push().getKey();

        final DatabaseReference messageReference = Firebase.getInstance().getDatabaseReference().child("Messages");

        Map chatMap = new HashMap();
        chatMap.put("last_message_id", message_id);
        chatMap.put("time", ServerValue.TIMESTAMP);
        chatMap.put("seen", false);
        chatMap.put("typing", false);

        final Map chatsMap = new HashMap();
        chatsMap.put("Chats/" + message.getFrom() + "/" + chatUid, chatMap);
        chatsMap.put("Chats/" + chatUid + "/" + message.getFrom(), chatMap);

        final Map messageMap = new HashMap();
        messageMap.put("from", message.getFrom());
        messageMap.put("message", message.getMessage());
        messageMap.put("url", message.getUrl());
        messageMap.put("type", message.getType());
        messageMap.put("send", message.isSend());
        messageMap.put("seen", message.isSeen());
        messageMap.put("receive", message.isReceive());
        messageMap.put("time", ServerValue.TIMESTAMP);

        messageReference.child(message.getFrom()).child(chatUid).child(message_id).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    messageReference.child(message.getFrom()).child(chatUid).child(message_id).child("send").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            messageReference.child(chatUid).child(message.getFrom()).child(message_id).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Firebase.getInstance().getDatabaseReference().updateChildren(chatsMap);
                                        messageReference.child(message.getFrom()).child(chatUid).child(message_id).child("receive").setValue(true);
                                        Insert.getInstance().notification(chatUid, "message", message.getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}

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

public class Text implements MessageStrategy {

    @Override
    public void send(final Message message, final String chatUid) {

        final String message_id = Firebase.getInstance().getDatabaseReference().child("Messages").child(message.getFrom()).child(chatUid).push().getKey();

        final DatabaseReference messageReference = Firebase.getInstance().getDatabaseReference().child("Messages");

        Map myChatMap = new HashMap();
        myChatMap.put("time", ServerValue.TIMESTAMP);
        myChatMap.put("seen", true);
        myChatMap.put("typing", false);

        Map chatMap = new HashMap();
        chatMap.put("time", ServerValue.TIMESTAMP);
        chatMap.put("seen", false);
        chatMap.put("typing", false);

        final Map chatsMap = new HashMap();
        chatsMap.put("Chats/" + message.getFrom() + "/" + chatUid, myChatMap);
        chatsMap.put("Chats/" + chatUid + "/" + message.getFrom(), chatMap);

        final Map messageMap = new HashMap();
        messageMap.put("from", message.getFrom());
        messageMap.put("message", message.getMessage());
        messageMap.put("url", message.getUrl());
        messageMap.put("type", message.getType());
        messageMap.put("send", message.isSend());
        messageMap.put("seen", message.isSeen());
        messageMap.put("receive", message.isReceive());
        messageMap.put("unsend", message.isUnsend());
        messageMap.put("time", ServerValue.TIMESTAMP);

        final Map myUpdate = new HashMap();
        myUpdate.put("send", true);
        myUpdate.put("seen", true);

        assert message_id != null;
        messageReference.child(message.getFrom()).child(chatUid).child(message_id).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    messageReference.child(message.getFrom()).child(chatUid).child(message_id).updateChildren(myUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
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

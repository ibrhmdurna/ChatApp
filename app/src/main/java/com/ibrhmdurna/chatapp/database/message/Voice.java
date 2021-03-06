package com.ibrhmdurna.chatapp.database.message;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.strategy.MessageStrategy;
import com.ibrhmdurna.chatapp.models.Message;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Voice implements MessageStrategy {
    @Override
    public void send(final Message message, final String chatUid) {
        String uid = FirebaseAuth.getInstance().getUid();

        final String message_id = Firebase.getInstance().getDatabaseReference().child("Messages").child(message.getFrom()).child(chatUid).push().getKey();

        final DatabaseReference messageReference = Firebase.getInstance().getDatabaseReference().child("Messages");

        Map myChatMap = new HashMap();
        myChatMap.put("time", ServerValue.TIMESTAMP);
        myChatMap.put("seen", true);
        myChatMap.put("typing", false);
        myChatMap.put("wave", true);

        Map chatMap = new HashMap();
        chatMap.put("time", ServerValue.TIMESTAMP);
        chatMap.put("seen", false);
        chatMap.put("typing", false);
        chatMap.put("wave", true);

        final Map chatsMap = new HashMap();
        chatsMap.put("Chats/" + message.getFrom() + "/" + chatUid, myChatMap);
        chatsMap.put("Chats/" + chatUid + "/" + message.getFrom(), chatMap);

        assert uid != null;
        assert message_id != null;
        final StorageReference myFilepath = FirebaseStorage.getInstance().getReference().child("Chats").child(uid).child(chatUid).child("Voice").child(message_id);
        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Chats").child(chatUid).child(uid).child("Voice").child(message_id);

        final Map myMessageMap = new HashMap();
        myMessageMap.put("from", message.getFrom());
        myMessageMap.put("message", message.getMessage());
        myMessageMap.put("url", "");
        myMessageMap.put("type", message.getType());
        myMessageMap.put("send", message.isSend());
        myMessageMap.put("seen", true);
        myMessageMap.put("receive", message.isReceive());
        myMessageMap.put("unsend", message.isUnsend());
        myMessageMap.put("path", message.getPath());
        myMessageMap.put("time", ServerValue.TIMESTAMP);
        myMessageMap.put("download", true);

        messageReference.child(message.getFrom()).child(chatUid).child(message_id).setValue(myMessageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Uri uri = Uri.fromFile(new File(message.getPath()));
                    myFilepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                myFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        myFilepath.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                            @Override
                                            public void onSuccess(final StorageMetadata storageMetadata) {

                                                Map updateMap = new HashMap();
                                                updateMap.put("size", storageMetadata.getSizeBytes());
                                                updateMap.put("url", downloadUrl);
                                                updateMap.put("send", true);

                                                messageReference.child(message.getFrom()).child(chatUid).child(message_id).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if(task.isSuccessful()){
                                                            Uri uri = Uri.fromFile(new File(message.getPath()));
                                                            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {
                                                                                String downloadUrl = uri.toString();

                                                                                final Map messageMap = new HashMap();
                                                                                messageMap.put("from", message.getFrom());
                                                                                messageMap.put("message", message.getMessage());
                                                                                messageMap.put("url", downloadUrl);
                                                                                messageMap.put("type", message.getType());
                                                                                messageMap.put("send", message.isSend());
                                                                                messageMap.put("seen", message.isSeen());
                                                                                messageMap.put("receive", message.isReceive());
                                                                                messageMap.put("unsend", message.isUnsend());
                                                                                messageMap.put("time", ServerValue.TIMESTAMP);
                                                                                messageMap.put("path", "");
                                                                                messageMap.put("download", message.isDownload());
                                                                                messageMap.put("size", storageMetadata.getSizeBytes());

                                                                                messageReference.child(chatUid).child(message.getFrom()).child(message_id).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            Firebase.getInstance().getDatabaseReference().updateChildren(chatsMap);
                                                                                            messageReference.child(message.getFrom()).child(chatUid).child(message_id).child("receive").setValue(true);
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
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
    }
}

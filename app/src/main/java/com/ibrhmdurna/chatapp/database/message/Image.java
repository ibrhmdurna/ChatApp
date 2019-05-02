package com.ibrhmdurna.chatapp.database.message;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.database.strategy.MessageStrategy;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.controller.FileController;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Image extends MessageStrategy {

    @Override
    public void Send(final Message message, final String chatUid) {

        String uid = FirebaseAuth.getInstance().getUid();

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

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        message.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] data = stream.toByteArray();

        final StorageReference myFilepath = FirebaseStorage.getInstance().getReference().child("Chats").child(uid).child(chatUid).child(message_id);
        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Chats").child(chatUid).child(uid).child(message_id);

        final Map myMessageMap = new HashMap();
        myMessageMap.put("from", message.getFrom());
        myMessageMap.put("message", message.getMessage());
        myMessageMap.put("url", "");
        myMessageMap.put("type", message.getType());
        myMessageMap.put("send", message.isSend());
        myMessageMap.put("seen", message.isSeen());
        myMessageMap.put("receive", message.isReceive());
        myMessageMap.put("path", "");
        myMessageMap.put("time", ServerValue.TIMESTAMP);
        myMessageMap.put("download", true);

        FileController.getInstance().compressToImageMessage(message.getBitmap(), chatUid, message_id);

        messageReference.child(message.getFrom()).child(chatUid).child(message_id).setValue(myMessageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    myFilepath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                myFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
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

                                                            filepath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {
                                                                                final String downloadUrl = uri.toString();

                                                                                final Map messageMap = new HashMap();
                                                                                messageMap.put("from", message.getFrom());
                                                                                messageMap.put("message", message.getMessage());
                                                                                messageMap.put("url", downloadUrl);
                                                                                messageMap.put("type", message.getType());
                                                                                messageMap.put("send", message.isSend());
                                                                                messageMap.put("seen", message.isSeen());
                                                                                messageMap.put("receive", message.isReceive());
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
                                                                                            Insert.getInstance().notification(chatUid, "image", message.getMessage(), downloadUrl);
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

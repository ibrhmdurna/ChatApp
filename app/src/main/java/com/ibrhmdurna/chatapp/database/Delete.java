package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.find.MessageFindAll;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.start.StartActivity;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.io.File;
import java.util.Objects;

public class Delete {

    private static Delete instance;

    private Delete(){}

    public static synchronized Delete getInstance() {
        if(instance == null){
            synchronized (Delete.class){
                instance = new Delete();
            }
        }
        return instance;
    }

    public void friend(final String id){
        final String uid = FirebaseAuth.getInstance().getUid();

        if(uid != null){
            Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).child(id).removeValue();
            Firebase.getInstance().getDatabaseReference().child("Friends").child(id).child(uid).removeValue();
        }
    }

    public void myRequest(final String id){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Request").child(id).child(uid).removeValue();
    }

    public void request(final String id){
        final String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).child(id).removeValue();
    }

    public void recent(String recent_id){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).child(recent_id).removeValue();
    }

    public void recentBlock(String id){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Recent").child(id).child(uid).removeValue();
    }

    public void allRecent(){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).removeValue();
    }

    public void deleteAccount(final Activity context, final TextInputLayout passwordInput){

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, context.getString(R.string.deleting_account));
        loadingBar.show();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), Objects.requireNonNull(passwordInput.getEditText()).getText().toString());

            passwordInput.setError(null);

            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        final String uid = FirebaseAuth.getInstance().getUid();

                        assert uid != null;
                        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    Account account = dataSnapshot.getValue(Account.class);

                                    if(account != null){
                                        String profile_image = account.getProfile_image();

                                        if(!profile_image.substring(0,8).equals("default_")){
                                            // Profile Image Deleted
                                            FirebaseStorage.getInstance().getReferenceFromUrl(account.getProfile_image()).delete();
                                            FirebaseStorage.getInstance().getReferenceFromUrl(account.getThumb_image()).delete();
                                        }

                                        // Request Deleted
                                        requestAllDelete(uid);

                                        // Friends Deleted
                                        friendAllDelete(uid);

                                        // Recent Deleted
                                        recentAllDelete(uid);

                                        // Chat Deleted
                                        chatAllDelete(uid);

                                        // Messages Deleted
                                        messageAllDelete(uid, true);

                                        dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                loadingBar.dismiss();

                                                                Intent startIntent = new Intent(context, StartActivity.class);
                                                                context.startActivity(startIntent);
                                                                context.finish();
                                                            }
                                                            else{
                                                                Toast.makeText(context, context.getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
                                                                loadingBar.dismiss();
                                                            }
                                                        }
                                                    });
                                                }
                                                else{
                                                    Toast.makeText(context, context.getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(context, context.getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }

                                }
                                else{
                                    Toast.makeText(context, context.getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                    else{
                        passwordInput.setError(context.getString(R.string.password_incorrect));
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    public void messageAllDelete(final String uid, final boolean device){
        Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);

                            if(message != null){
                                message.setMessage_id(snapshot1.getKey());

                                if(message.getType().equals("Text")){
                                    message(message, snapshot.getKey());
                                }
                                else if(message.getType().equals("Image")){
                                    if(device){
                                        File file = new File(message.getPath());

                                        if(file.exists()){
                                            file.delete();
                                        }
                                    }

                                    imageMessage(message, snapshot.getKey());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void chatAllDelete(final String uid){
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).removeValue();
    }

    private void recentAllDelete(final String uid){
        Firebase.getInstance().getDatabaseReference().child("Recent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                        String recent_uid = snapshot1.getKey();
                                        assert recent_uid != null;
                                        if(recent_uid.equals(uid)){
                                            snapshot1.getRef().removeValue();
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    dataSnapshot.getRef().child(uid).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void friendAllDelete(final String uid){
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String friend_id = snapshot.getKey();
                        assert friend_id != null;
                        Firebase.getInstance().getDatabaseReference().child("Friends").child(friend_id).child(uid).removeValue();
                    }

                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void requestAllDelete(final String uid){
        Firebase.getInstance().getDatabaseReference().child("Request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            if(Objects.equals(snapshot1.getKey(), uid)){
                                snapshot1.getRef().removeValue();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void unblock(final String id){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Blocks").child(uid).child(id).removeValue();
    }

    public void message(Message message, String chatUid){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        MessageFindAll.isRemoved = true;
        Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).child(message.getMessage_id()).removeValue();
    }

    public void imageMessage(Message message, String chatUid){
        if(!message.getUrl().equals(""))
            FirebaseStorage.getInstance().getReferenceFromUrl(message.getUrl()).delete();

        message(message, chatUid);
    }

    public void clearChat(String chatUid, boolean deleteDevice){
        allDeleteImage(chatUid, deleteDevice);
    }

    public void deleteChat(String chatUid, boolean deleteDevice){
        String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        clearChat(chatUid, deleteDevice);
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).child(chatUid).removeValue();
    }

    private void allDeleteImage(final String chatUid, final boolean deleteDevice){
        final String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Message message = snapshot.getValue(Message.class);

                        if(message != null && message.getType().equals("Image")){
                            FirebaseStorage.getInstance().getReferenceFromUrl(message.getUrl()).delete();

                            if(deleteDevice && !message.getPath().equals("")){
                                File file = new File(message.getPath());

                                if(file.exists()){
                                    file.delete();
                                }
                            }
                        }
                    }

                    Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deviceToken(String id){
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(id).child("device_token").removeValue();
    }
}

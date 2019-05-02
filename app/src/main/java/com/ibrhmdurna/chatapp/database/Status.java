package com.ibrhmdurna.chatapp.database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

public class Status {

    private static Status instance;

    private Status(){}

    public static synchronized Status getInstance(){
        if(instance == null){
            synchronized (Status.class){
                instance = new Status();
            }
        }
        return instance;
    }

    public void onConnect(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Firebase.getInstance().getDatabaseReference().child("Accounts").child(currentUser.getUid()).child("online").setValue(true);
        }
    }

    public void onDisconnect(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Firebase.getInstance().getDatabaseReference().child("Accounts").child(currentUser.getUid()).child("online").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Firebase.getInstance().getDatabaseReference().child("Accounts").child(currentUser.getUid()).child("last_seen").setValue(ServerValue.TIMESTAMP);
                    }
                }
            });
        }
    }
}

package com.ibrhmdurna.chatapp.database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class Connection {

    private static Connection instance;

    private Connection (){}

    public static synchronized Connection getInstance(){
        if(instance == null){
            synchronized (Connection.class){
                instance = new Connection();
            }
        }
        return instance;
    }

    public void onConnect(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);

            databaseReference.child("Accounts").child(currentUser.getUid()).child("online").setValue(true);
        }
    }

    public void onDisconnect(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);

            databaseReference.child("Accounts").child(currentUser.getUid()).child("online").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        databaseReference.child("Accounts").child(currentUser.getUid()).child("last_seen").setValue(ServerValue.TIMESTAMP);
                    }
                }
            });
        }
    }
}

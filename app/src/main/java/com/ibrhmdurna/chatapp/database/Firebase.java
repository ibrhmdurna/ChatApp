package com.ibrhmdurna.chatapp.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {

    private DatabaseReference databaseReference;

    private static Firebase instance;

    public static synchronized Firebase getInstance(){
        if(instance == null){
            synchronized (Firebase.class){
                instance = new Firebase();
            }
        }
        return instance;
    }

    public DatabaseReference getDatabaseReference() {
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);
        }
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }
}

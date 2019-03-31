package com.ibrhmdurna.chatapp.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference database;

    private static FirebaseDB instance;

    private FirebaseDB() {
    }

    public static synchronized FirebaseDB getInstance() {
        if(instance == null){
            synchronized (FirebaseDB.class){
                instance = new FirebaseDB();
            }
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        if(this.auth == null)
            this.auth = FirebaseAuth.getInstance();
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        if(this.currentUser == null)
            this.currentUser = getAuth().getCurrentUser();
        return currentUser;
    }

    public DatabaseReference getDatabase() {
        if(this.database == null)
            this.database = FirebaseDatabase.getInstance().getReference();
        return database;
    }
}

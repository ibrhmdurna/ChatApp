package com.ibrhmdurna.chatapp.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseDB {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference database;
    private StorageReference storage;

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

    public StorageReference getStorage() {
        if(this.storage == null)
            this.storage = FirebaseStorage.getInstance().getReference();
        return storage;
    }
}

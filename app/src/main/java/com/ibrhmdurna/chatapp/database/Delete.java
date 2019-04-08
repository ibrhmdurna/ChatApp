package com.ibrhmdurna.chatapp.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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

    public void deleteRecent(String recent_id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).child(recent_id).setValue(null);
    }

    public void deleteAllRecent(){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).setValue(null);
    }
}

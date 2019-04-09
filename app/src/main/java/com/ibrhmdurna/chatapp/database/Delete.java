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

    public void friend(String id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).child(id).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Friends").child(id).child(uid).removeValue();
    }

    public void myRequest(String id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Request").child(uid).child(id).removeValue();
    }

    public void request(String id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Request").child(id).child(uid).removeValue();
    }

    public void recent(String recent_id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).child(recent_id).removeValue();
    }

    public void allRecent(){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).removeValue();
    }
}

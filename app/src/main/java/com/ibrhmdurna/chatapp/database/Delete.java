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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.start.StartActivity;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

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

        FirebaseDatabase.getInstance().getReference().child("Request").child(id).child(uid).removeValue();
    }

    public void request(String id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Request").child(uid).child(id).removeValue();
    }

    public void recent(String recent_id){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).child(recent_id).removeValue();
    }

    public void allRecent(){
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).removeValue();
    }

    public void deleteAccount(final Activity context, final TextInputLayout passwordInput){

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, "Deleting account. Please wait...");
        loadingBar.show();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), passwordInput.getEditText().getText().toString());

        passwordInput.setError(null);

        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    final String uid = FirebaseAuth.getInstance().getUid();

                    FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                Account account = dataSnapshot.getValue(Account.class);

                                String profile_image = account.getProfile_image();

                                if(!profile_image.substring(0,8).equals("default_")){
                                    // Profile Image Deleted
                                    FirebaseStorage.getInstance().getReferenceFromUrl(account.getProfile_image()).delete();
                                    FirebaseStorage.getInstance().getReferenceFromUrl(account.getThumb_image()).delete();
                                }


                                // Friends Deleted
                                FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                String friend_id = snapshot.getKey();
                                                FirebaseDatabase.getInstance().getReference().child("Friends").child(friend_id).child(uid).removeValue();
                                            }

                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                // Recent Deleted
                                FirebaseDatabase.getInstance().getReference().child("Recent").addListenerForSingleValueEvent(new ValueEventListener() {
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

                                dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            loadingBar.dismiss();

                                            Intent startIntent = new Intent(context, StartActivity.class);
                                            context.startActivity(startIntent);
                                            context.finish();
                                        }
                                        else{
                                            Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });

                            }
                            else{
                                Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else{
                    passwordInput.setError("Password is incorrect.");
                    loadingBar.dismiss();
                }
            }
        });
    }
}

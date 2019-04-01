package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.io.ByteArrayOutputStream;

public class Update {

    private static Update instance;

    private Update(){}

    public static Update getInstance() {
        if(instance == null){
            synchronized (Update.class){
                instance = new Update();
            }
        }
        return instance;
    }

    public void updateAccount(final Activity context, final Account account, final Bitmap bitmap, final boolean feed){

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context);
        loadingBar.show();

        final String uid = FirebaseDB.getInstance().getCurrentUser().getUid();

        if(feed){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final StorageReference filepath = FirebaseDB.getInstance().getStorage().child("Accounts").child(uid).child("profile_image");

            filepath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                account.setProfile_image(downloadUrl);
                                FirebaseDB.getInstance().getDatabase().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            context.finish();
                                        }
                                        else {
                                            Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                                        }
                                        loadingBar.dismiss();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            FirebaseDB.getInstance().getDatabase().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        context.finish();
                    }
                    else {
                        Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }


    }
}

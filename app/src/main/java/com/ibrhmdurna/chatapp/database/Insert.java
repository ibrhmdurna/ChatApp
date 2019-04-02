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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.io.ByteArrayOutputStream;

public class Insert {

    private static Insert instance;

    private Insert(){
    }

    public static synchronized Insert getInstance() {
        if(instance == null){
            synchronized (Insert.class){
                instance = new Insert();
            }
        }
        return instance;
    }

    public void register(final Account account, String password, final Bitmap bitmap, final Activity context){
        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, "Creating an account may take some time.\nPlease Waiting... ");
        loadingBar.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(account.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if(bitmap != null){
                        Bitmap thumb_bitmap = bitmap;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        final byte[] thumb_byte = baos.toByteArray();

                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                        byte[] data = baos2.toByteArray();

                        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Accounts").child(uid).child("profile_image");
                        final StorageReference thumb_filepath = FirebaseStorage.getInstance().getReference().child("Accounts").child(uid).child("thumb_profile_image");

                        filepath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            account.setProfile_image(downloadUrl);

                                            thumb_filepath.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if(task.isSuccessful()){

                                                        thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String thumbDownloadUrl = uri.toString();
                                                                account.setThumb_image(thumbDownloadUrl);

                                                                FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            loadingBar.dismiss();
                                                                            Intent mainIntent = new Intent(context, MainActivity.class);
                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            context.startActivity(mainIntent);
                                                                            context.finish();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                loadingBar.dismiss();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                    loadingBar.dismiss();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                            loadingBar.dismiss();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        });
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Intent mainIntent = new Intent(context, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(mainIntent);
                                    context.finish();
                                }
                            }
                        });
                    }
                }
                else {
                    loadingBar.dismiss();
                    DialogController.getInstance().dialogError(context);
                }
            }
        });
    }
}

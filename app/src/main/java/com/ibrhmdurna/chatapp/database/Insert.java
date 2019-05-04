package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.controller.AppController;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, context.getString(R.string.creating_account));
        loadingBar.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(account.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(account.getNameSurname()).build();

                    currentUser.updateProfile(profileChangeRequest);

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

                                                                Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            loadingBar.dismiss();

                                                                            deviceToken(FirebaseAuth.getInstance().getUid());

                                                                            Intent mainIntent = new Intent(context, MainActivity.class);
                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                            mainIntent.putExtra("page", "Main");
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
                        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Intent mainIntent = new Intent(context, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mainIntent.putExtra("page", "Main");
                                    context.startActivity(mainIntent);
                                    context.finish();
                                }
                            }
                        });
                    }
                }
                else {
                    loadingBar.dismiss();
                    final AlertDialog dialog = DialogController.getInstance().dialogCustom(context, context.getString(R.string.cannot_sign_in), null, context.getString(R.string.dismiss));
                    TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void recent(String id){

        String uid = FirebaseAuth.getInstance().getUid();

        Map recentMap = new HashMap();
        recentMap.put("time", ServerValue.TIMESTAMP);

        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).child(id).setValue(recentMap);

    }

    public void request(final String id){

        String uid = FirebaseAuth.getInstance().getUid();

        Map requestMap = new HashMap();
        requestMap.put("Request/" + id + "/" + uid + "/seen", false);
        requestMap.put("Request/" + id + "/" + uid + "/time", ServerValue.TIMESTAMP);

        Firebase.getInstance().getDatabaseReference().updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    notification(id, "request");
                }
            }
        });
    }

    public void friend(final String id){
        String uid = FirebaseAuth.getInstance().getUid();

        String currentDate = java.text.DateFormat.getDateInstance().format(new Date());

        Map friendMap = new HashMap();
        friendMap.put("Friends/" + uid + "/" + id + "/date", currentDate);
        friendMap.put("Friends/" + uid + "/" + id + "/time", ServerValue.TIMESTAMP);
        friendMap.put("Friends/" + id + "/" + uid + "/date", currentDate);
        friendMap.put("Friends/" + id + "/" + uid + "/time", ServerValue.TIMESTAMP);

        friendMap.put("Request/" + uid + "/" + id + "/seen", null);
        friendMap.put("Request/" + uid + "/" + id + "/time", null);
        friendMap.put("Request/" + id + "/" + uid + "/seen", null);
        friendMap.put("Request/" + id + "/" + uid + "/time", null);

        Firebase.getInstance().getDatabaseReference().updateChildren(friendMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                notification(id, "confirm");
            }
        });
    }

    public void deviceToken(String id){
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(id).child("device_token").setValue(deviceToken);
    }

    public void notification(String id, String type){
        String uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = Firebase.getInstance().getDatabaseReference().child("Notifications").child(id);
        String notification_id = databaseReference.push().getKey();

        Map notificationMap = new HashMap();
        notificationMap.put("from", uid);
        notificationMap.put("type", type);

        databaseReference.child(notification_id).setValue(notificationMap);
    }

    public void notification(String id, String type, String message){
        String uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = Firebase.getInstance().getDatabaseReference().child("Notifications").child(id);
        String notification_id = databaseReference.push().getKey();

        Map notificationMap = new HashMap();
        notificationMap.put("from", uid);
        notificationMap.put("type", type);
        notificationMap.put("message", message);

        databaseReference.child(notification_id).setValue(notificationMap);
    }

    public void notification(String id, String type, String message, String image){
        String uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = Firebase.getInstance().getDatabaseReference().child("Notifications").child(id);
        String notification_id = databaseReference.push().getKey();

        Map notificationMap = new HashMap();
        notificationMap.put("from", uid);
        notificationMap.put("type", type);
        notificationMap.put("message", message);
        notificationMap.put("image", image);

        databaseReference.child(notification_id).setValue(notificationMap);
    }
}

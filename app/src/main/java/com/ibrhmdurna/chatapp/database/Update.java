package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.controller.AppController;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.io.ByteArrayOutputStream;

public class Update{

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

    public void updateAccount(final Activity context, final Account account, final Bitmap bitmap){

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, "This may take some time.\nPlease keep waiting... ");
        loadingBar.show();

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(account.getNameSurname()).build();

        currentUser.updateProfile(profileChangeRequest);

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
                                                                Intent accountIntent = new Intent(context, MainActivity.class);
                                                                accountIntent.putExtra("page","Account");
                                                                accountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                context.startActivity(accountIntent);
                                                                Toast.makeText(context, "Information was successfully update.", Toast.LENGTH_SHORT).show();
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
                        Intent accountIntent = new Intent(context, MainActivity.class);
                        accountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        accountIntent.putExtra("page","Account");
                        context.startActivity(accountIntent);
                        Toast.makeText(context, "Information was successfully update.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }


    }

    public void updatePassword(final Activity context, final TextInputLayout currentPasswordInput, final TextInputLayout newPasswordInput, final TextInputLayout confirmPasswordInput){

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, "Please keep waiting...");
        loadingBar.show();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPasswordInput.getEditText().getText().toString());

        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(!currentPasswordInput.getEditText().getText().toString().equals(newPasswordInput.getEditText().getText().toString())){

                        currentUser.updatePassword(confirmPasswordInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    confirmPasswordInput.getEditText().getText().clear();
                                    newPasswordInput.getEditText().getText().clear();
                                    currentPasswordInput.getEditText().getText().clear();
                                    confirmPasswordInput.getEditText().clearFocus();
                                    Toast.makeText(context, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    loadingBar.dismiss();
                                    Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else {
                        loadingBar.dismiss();
                        newPasswordInput.setError("The new password can not be the same as the old password.");
                    }
                }
                else {
                    loadingBar.dismiss();
                    currentPasswordInput.setError("Current password is incorrect.");
                }
            }
        });
    }

    public void resetPassword(Activity context, final TextInputLayout emailInput, final LinearLayout sendLayout, final LottieAnimationView doneAnim, final TextView successText){

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, "Sending...");
        loadingBar.show();

        emailInput.setError(null);

        FirebaseAuth.getInstance().useAppLanguage();

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                    fadeIn.setDuration(1000);
                    sendLayout.setAnimation(fadeIn);
                    sendLayout.setVisibility(View.VISIBLE);
                    successText.setText("We sent mail to " + emailInput.getEditText().getText().toString());
                    doneAnim.playAnimation();
                    emailInput.getEditText().getText().clear();
                    emailInput.getEditText().clearFocus();

                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation fadeOut = new AlphaAnimation(1, 0);
                            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                            fadeOut.setStartOffset(1000);
                            fadeOut.setDuration(1000);
                            sendLayout.setAnimation(fadeOut);
                            sendLayout.setVisibility(View.GONE);
                        }
                    }, 3000);

                }
                else{
                    loadingBar.dismiss();
                    emailInput.setError("Email is incorrect. Please enter a valid email address.");
                }
            }
        });
    }

    public void updateEmail(final Activity context, final TextInputLayout newEmailInput, final TextInputLayout passwordInput){

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loadingBar = DialogController.getInstance().dialogLoading(context, "Please keep waiting...");
        loadingBar.show();

        newEmailInput.setError(null);
        passwordInput.setError(null);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), passwordInput.getEditText().getText().toString());

        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(newEmailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().getSignInMethods().size() > 0){
                                    loadingBar.dismiss();
                                    newEmailInput.setError("This email is used");
                                }
                                else {

                                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(newEmailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                String uid = FirebaseAuth.getInstance().getUid();

                                                FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).child("email").setValue(newEmailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){
                                                            loadingBar.dismiss();
                                                            newEmailInput.getEditText().getText().clear();
                                                            passwordInput.getEditText().getText().clear();
                                                            passwordInput.getEditText().clearFocus();
                                                            Toast.makeText(context, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            loadingBar.dismiss();
                                                            Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                            }
                                            else {
                                                loadingBar.dismiss();
                                                Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }

                            }
                            else {
                                loadingBar.dismiss();
                                newEmailInput.setError("Please enter a valid email address.");
                            }
                        }
                    });

                }
                else {
                    loadingBar.dismiss();
                    passwordInput.setError("Current password is incorrect.");
                }
            }
        });
    }
}

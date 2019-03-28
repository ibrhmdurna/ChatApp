package com.ibrhmdurna.chatapp.Database;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.ibrhmdurna.chatapp.Main.MainActivity;
import com.ibrhmdurna.chatapp.Models.Account;
import com.ibrhmdurna.chatapp.Util.DialogController;

public class Insert extends FirebaseDB {

    private static Insert instance;

    private Insert(){
    }

    public static Insert getInstance() {
        if(instance == null){
            synchronized (Insert.class){
                instance = new Insert();
            }
        }
        return instance;
    }

    public void register(final Account account, String password, final Activity context){
        final AlertDialog loading = DialogController.dialogLoading(context);
        loading.show();

        this.getAuth().createUserWithEmailAndPassword(account.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String uid = getCurrentUser().getUid();

                    getDatabase().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                loading.dismiss();
                                Intent mainIntent = new Intent(context, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(mainIntent);
                                context.finish();
                            }
                        }
                    });
                }
                else {
                    loading.dismiss();
                    DialogController.dialogError(context);
                }
            }
        });
    }
}

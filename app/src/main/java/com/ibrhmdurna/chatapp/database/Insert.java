package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

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

    public void register(final Account account, String password, final Activity context){
        final AlertDialog loading = DialogController.getInstance().dialogLoading(context);
        loading.show();

        FirebaseDB.getInstance().getAuth().createUserWithEmailAndPassword(account.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String uid = FirebaseDB.getInstance().getCurrentUser().getUid();

                    FirebaseDB.getInstance().getDatabase().child("Accounts").child(uid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    DialogController.getInstance().dialogError(context);
                }
            }
        });
    }
}

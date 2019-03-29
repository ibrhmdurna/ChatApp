package com.ibrhmdurna.chatapp.Database;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.ibrhmdurna.chatapp.Main.MainActivity;
import com.ibrhmdurna.chatapp.Start.RegisterInfoActivity;
import com.ibrhmdurna.chatapp.Util.AppController;
import com.ibrhmdurna.chatapp.Util.DialogController;

public class Search{

    private static Search instance;

    private Search(){}

    public static Search getInstance() {
        if(instance == null){
            synchronized (Search.class){
                instance = new Search();
            }
        }
        return instance;
    }

    public void checkEmail(final Activity context, final TextInputLayout emailInput, final TextInputLayout passwordInput){

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loading = DialogController.dialogLoading(context);
        loading.show();

        FirebaseDB.getInstance().getAuth().fetchSignInMethodsForEmail(emailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getSignInMethods().size() > 0){
                        emailInput.setError("This email is used");
                    }
                    else {
                        Intent infoIntent = new Intent(context, RegisterInfoActivity.class);
                        infoIntent.putExtra("email", emailInput.getEditText().getText().toString());
                        infoIntent.putExtra("password", passwordInput.getEditText().getText().toString());
                        context.startActivity(infoIntent);
                    }

                }
                else {
                    loading.dismiss();
                    AlertDialog errorDialog = DialogController.dialogError(context);
                    errorDialog.show();
                }

                loading.dismiss();
            }
        });

    }

    public void login(final Activity context, TextInputLayout emailInput, TextInputLayout passwordInput){
        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loading = DialogController.dialogLoading(context);
        loading.show();

        FirebaseDB.getInstance().getAuth().signInWithEmailAndPassword(emailInput.getEditText().getText().toString(), passwordInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(mainIntent);
                    context.finish();
                }
                else{
                    loading.dismiss();
                    AlertDialog errorDialog = DialogController.dialogError(context);
                    errorDialog.show();
                }

                loading.dismiss();
            }
        });
    }
}

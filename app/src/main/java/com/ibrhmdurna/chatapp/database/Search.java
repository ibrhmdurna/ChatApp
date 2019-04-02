package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.start.RegisterInfoActivity;
import com.ibrhmdurna.chatapp.util.controller.AppController;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import static android.content.Context.MODE_PRIVATE;

public class Search {

    private static Search instance;

    private Search(){}

    public static synchronized Search getInstance() {
        if(instance == null){
            synchronized (Search.class){
                instance = new Search();
            }
        }
        return instance;
    }

    public void checkEmail(final Activity context, final TextInputLayout emailInput, final TextInputLayout passwordInput){

        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loading = DialogController.getInstance().dialogLoading(context, "Please Waiting...");
        loading.show();

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(emailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
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
                        emailInput.setError(null);
                    }

                }
                else {
                    loading.dismiss();
                    AlertDialog errorDialog = DialogController.getInstance().dialogError(context);
                    errorDialog.show();
                }

                loading.dismiss();
            }
        });

    }

    public void login(final Activity context, final TextInputLayout emailInput, final TextInputLayout passwordInput, final CheckBox remember){
        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loading = DialogController.getInstance().dialogLoading(context, "Please Waiting...");
        loading.show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailInput.getEditText().getText().toString(), passwordInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    SharedPreferences prefs = context.getSharedPreferences("REMEMBER", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    if (remember.isChecked()) {
                        editor.putString("EMAIL", emailInput.getEditText().getText().toString());
                        editor.putString("PASSWORD", passwordInput.getEditText().getText().toString());

                        App.Remember.getInstance().setEmail(emailInput.getEditText().getText().toString());
                        App.Remember.getInstance().setPassword(passwordInput.getEditText().getText().toString());
                    }
                    else {
                        editor.putString("EMAIL", null);
                        editor.putString("PASSWORD", null);

                        App.Remember.getInstance().setEmail(null);
                        App.Remember.getInstance().setPassword(null);
                    }

                    editor.apply();

                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(mainIntent);
                    context.finish();
                }
                else{
                    loading.dismiss();
                    AlertDialog errorDialog = DialogController.getInstance().dialogError(context);
                    errorDialog.show();
                }

                loading.dismiss();
            }
        });
    }
}

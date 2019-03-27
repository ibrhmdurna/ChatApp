package com.ibrhmdurna.chatapp.Database;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.ibrhmdurna.chatapp.Start.RegisterInfoActivity;
import com.ibrhmdurna.chatapp.Utils.AppController;
import com.ibrhmdurna.chatapp.Utils.DialogController;

public class Search extends FirebaseDB {

    public Search(){}

    public void checkEmail(final Activity context, final TextInputLayout emailInput, final TextInputLayout passwordInput, final ProgressBar loadingBar){

        AppController.closeKeyboard(context);

        final AlertDialog loading = DialogController.dialogLoading(context);
        loading.show();

        this.getAuth().fetchSignInMethodsForEmail(emailInput.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
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
                    AlertDialog errorDialog = DialogController.dialogError(context);
                    errorDialog.show();
                }

                loading.dismiss();
            }
        });

    }
}

package com.ibrhmdurna.chatapp.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.start.RegisterInfoActivity;
import com.ibrhmdurna.chatapp.util.adapter.SearchAdapter;
import com.ibrhmdurna.chatapp.util.controller.AppController;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.util.List;
import java.util.Objects;

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

        final AlertDialog loading = DialogController.getInstance().dialogLoading(context, context.getString(R.string.please_wait));
        loading.show();

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(Objects.requireNonNull(emailInput.getEditText()).getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    loading.dismiss();
                    if(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() > 0){
                        emailInput.setError(context.getString(R.string.email_is_used));
                    }
                    else {
                        Intent infoIntent = new Intent(context, RegisterInfoActivity.class);
                        infoIntent.putExtra("email", emailInput.getEditText().getText().toString());
                        infoIntent.putExtra("password", Objects.requireNonNull(passwordInput.getEditText()).getText().toString());
                        context.startActivity(infoIntent);
                        emailInput.setError(null);
                    }

                }
                else {
                    loading.dismiss();
                    emailInput.setError(context.getString(R.string.email_incorrect));
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                //Toast.makeText(context, context.getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void login(final Activity context, final TextInputLayout emailInput, final TextInputLayout passwordInput, final CheckBox remember){
        AppController.getInstance().closeKeyboard(context);

        final AlertDialog loading = DialogController.getInstance().dialogLoading(context, context.getString(R.string.please_wait));
        loading.show();

        emailInput.setError(null);
        passwordInput.setError(null);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(emailInput.getEditText()).getText().toString(), Objects.requireNonNull(passwordInput.getEditText()).getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

                    Insert.getInstance().deviceToken(FirebaseAuth.getInstance().getUid());

                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mainIntent.putExtra("page", "Main");
                    context.startActivity(mainIntent);
                    context.finish();
                }
                else{

                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();

                    switch (errorCode){
                        case "ERROR_INVALID_EMAIL":
                            emailInput.setError(context.getString(R.string.enter_a_vaild_email));
                            break;
                        case "ERROR_WRONG_PASSWORD":
                            passwordInput.setError(context.getString(R.string.password_incorrect));
                            break;
                        case "ERROR_USER_NOT_FOUND":
                            emailInput.setError(context.getString(R.string.no_account_for_this_email));
                            break;
                    }

                }

                loading.dismiss();
            }
        });
    }

    public void searchAccount(final String search, final SearchAdapter searchAdapter, final List<Account> accountList, final NestedScrollView searchLayout, final SpinKitView loadingBar){

        loadingBar.setIndeterminate(true);
        loadingBar.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);

        final String uid = FirebaseAuth.getInstance().getUid();

        Query query = Firebase.getInstance().getDatabaseReference().child("Accounts")
                .orderByChild("search_name")
                .startAt(search.toLowerCase())
                .endAt(search.toLowerCase() + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                accountList.clear();
                final int[] count = {0};
                if(dataSnapshot.exists()){
                    for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                        assert uid != null;
                        Firebase.getInstance().getDatabaseReference().child("Blocks").child(Objects.requireNonNull(snapshot.getKey())).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    count[0]++;
                                    Account account = new Account();
                                    account.setName(search);
                                    account.setUid("False");
                                    accountList.add(account);
                                }
                                else{
                                    count[0]++;
                                    Account account = snapshot.getValue(Account.class);
                                    if(account != null){
                                        account.setUid(snapshot.getKey());
                                        accountList.add(account);
                                    }
                                }

                                if(count[0] >= dataSnapshot.getChildrenCount()){
                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(accountList.size() > 0){
                                                searchLayout.setVisibility(View.VISIBLE);
                                            }
                                            else {
                                                searchLayout.setVisibility(View.GONE);
                                            }
                                            loadingBar.setIndeterminate(false);
                                            loadingBar.setVisibility(View.GONE);
                                            searchAdapter.notifyDataSetChanged();
                                        }
                                    },1000);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    count[0]++;
                    Account account = new Account();
                    account.setName(search);
                    account.setUid("False");
                    accountList.add(account);

                    if(count[0] >= dataSnapshot.getChildrenCount()){
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(accountList.size() > 0){
                                    searchLayout.setVisibility(View.VISIBLE);
                                }
                                else {
                                    searchLayout.setVisibility(View.GONE);
                                }
                                loadingBar.setIndeterminate(false);
                                loadingBar.setVisibility(View.GONE);
                                searchAdapter.notifyDataSetChanged();
                            }
                        },1000);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.ibrhmdurna.chatapp.start;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.Search;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.settings.ForgotActivity;
import com.ibrhmdurna.chatapp.util.Environment;

public class LoginActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout emailInput, passwordInput;
    private CheckBox rememberCheck;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolsManagement();
    }

    private void inputProcess(){
        emailInput.getEditText().addTextChangedListener(inputWatcher);
        passwordInput.getEditText().addTextChangedListener(inputWatcher);
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkInput();
        }
    };

    private void checkRemember(){
        String email = App.Remember.getInstance().getEmail();
        String password = App.Remember.getInstance().getPassword();

        if(email != null && password != null){
            emailInput.getEditText().setText(email);
            passwordInput.getEditText().setText(password);
            rememberCheck.setChecked(true);
        }
    }

    private void checkInput(){
        String email = emailInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();

        if(email.trim().length() > 0 && password.trim().length() > 0){
            loginBtn.setEnabled(true);
        }
        else {
            loginBtn.setEnabled(false);
        }
    }

    @Override
    public void buildView(){
        emailInput = findViewById(R.id.login_email_input);
        passwordInput = findViewById(R.id.login_password_input);
        rememberCheck = findViewById(R.id.login_remember_check);
        loginBtn = findViewById(R.id.login_btn);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.login_toolbar);
        buildView();
        inputProcess();
        checkRemember();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                Search.getInstance().login(this, emailInput, passwordInput, rememberCheck);
                break;
            case R.id.login_forgot_password_btn:
                Intent forgotIntent = new Intent(this, ForgotActivity.class);
                startActivity(forgotIntent);
                break;
        }
    }
}

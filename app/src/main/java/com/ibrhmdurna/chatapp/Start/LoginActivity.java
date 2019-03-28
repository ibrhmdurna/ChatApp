package com.ibrhmdurna.chatapp.Start;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.Main.MainActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Settings.ForgotActivity;
import com.ibrhmdurna.chatapp.Util.Environment;

public class LoginActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout emailInput, passwordInput;
    private CheckBox showPassCheck;
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

    private void showCheckPassword(){
        showPassCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    passwordInput.getEditText().setTransformationMethod(new PasswordTransformationMethod());
                else
                    passwordInput.getEditText().setTransformationMethod(null);
            }
        });
    }

    @Override
    public void buildView(){
        emailInput = findViewById(R.id.login_email_input);
        passwordInput = findViewById(R.id.login_password_input);
        showPassCheck = findViewById(R.id.login_show_pass_check);
        loginBtn = findViewById(R.id.login_btn);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.login_toolbar);
        buildView();
        showCheckPassword();
        inputProcess();
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
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
            case R.id.login_forgot_password_btn:
                Intent forgotIntent = new Intent(this, ForgotActivity.class);
                startActivity(forgotIntent);
                break;
        }
    }
}

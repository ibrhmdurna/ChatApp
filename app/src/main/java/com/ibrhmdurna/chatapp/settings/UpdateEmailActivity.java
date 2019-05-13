package com.ibrhmdurna.chatapp.settings;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.AppController;

import java.util.Objects;

public class UpdateEmailActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout newEmailInput, passwordInput;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        toolsManagement();
    }

    private void inputProcess(){
        Objects.requireNonNull(newEmailInput.getEditText()).addTextChangedListener(inputWatcher);
        Objects.requireNonNull(passwordInput.getEditText()).addTextChangedListener(inputWatcher);
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
        if(Objects.requireNonNull(newEmailInput.getEditText()).getText().length() > 0 && Objects.requireNonNull(passwordInput.getEditText()).getText().length() > 0){
            updateBtn.setEnabled(true);
        }
        else {
            updateBtn.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void buildView() {
        newEmailInput = findViewById(R.id.update_email_new_mail_input);
        passwordInput = findViewById(R.id.update_email_password_input);
        updateBtn = findViewById(R.id.update_btn);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.update_email_toolbar);
        buildView();
        inputProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_btn:
                AppController.getInstance().closeKeyboard(this);
                Update.getInstance().updateEmail(this, newEmailInput, passwordInput);
                break;
        }
    }
}

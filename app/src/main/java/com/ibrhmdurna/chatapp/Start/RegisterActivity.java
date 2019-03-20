package com.ibrhmdurna.chatapp.Start;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout emailInput, passwordInput, confirmInput;
    private TextView nextView;
    private ImageView passwordImage, confirmImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolsManagement();
    }

    private void checkInput(){
        String email = emailInput.getEditText().getText().toString();

        if(email.trim().length() > 0 && confirmInput.getHelperText().toString().equals("* Password are the same")){
            nextView.setEnabled(true);
        }
        else {
            nextView.setEnabled(false);
        }
    }

    private void passwordProcess(){
        passwordInput.getEditText().addTextChangedListener(passwordWatcher);
        confirmInput.getEditText().addTextChangedListener(passwordWatcher);
    }

    private TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(passwordInput.getEditText().getText().length() > 5){
                if(confirmInput.getEditText().getText().length() > 5){
                    if(confirmInput.getEditText().getText().toString().equals(passwordInput.getEditText().getText().toString())){
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        confirmInput.setHelperText("* Password are the same");
                        confirmInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                    }else {
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_fail_icon));
                        confirmInput.setHelperText("* Password are different");
                        confirmInput.setHelperTextColor(getColorStateList(R.color.colorFail));
                    }
                }else {
                    if(passwordInput.getEditText().getText().length() > 10){
                        passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        passwordInput.setHelperText("* Password is good");
                        passwordInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                    }
                    else if(passwordInput.getEditText().getText().length() > 5){
                        passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_warning_icon));
                        passwordInput.setHelperText("* Password is medium");
                        passwordInput.setHelperTextColor(getColorStateList(R.color.colorWarning));
                    }
                    else {
                        passwordInput.setHelperText(null);
                    }
                    //passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmInput.setHelperText("* Password must be at least 6 characters");
                    confirmInput.setHelperTextColor(getColorStateList(android.R.color.tab_indicator_text));
                }
            }else {
                passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                passwordInput.setHelperText(null);
                confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                confirmInput.setHelperText("* Password must be at least 6 characters");
                confirmInput.setHelperTextColor(getColorStateList(android.R.color.tab_indicator_text));
            }
            checkInput();
        }
    };

    private void buildView(){
        emailInput = findViewById(R.id.reg_email_input);
        passwordInput = findViewById(R.id.reg_password_input);
        confirmInput = findViewById(R.id.reg_confirm_input);
        nextView = findViewById(R.id.register_next_btn);
        passwordImage = findViewById(R.id.reg_password_image);
        confirmImage = findViewById(R.id.reg_confirm_image);
    }

    private void toolsManagement(){
        Environment.toolbarProcess(this, R.id.register_toolbar);
        buildView();
        passwordProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_next_btn:
                Intent finishIntent = new Intent(this, RegisterInfoActivity.class);
                startActivity(finishIntent);
                break;
        }
    }
}

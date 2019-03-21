package com.ibrhmdurna.chatapp.Settings;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;

public class SecurityActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private ImageView currentImage, newPassImage, confirmImage;
    private TextView changeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        toolsManagement();
    }

    private void checkInput(){
        String currentPass = currentPasswordInput.getEditText().getText().toString();

        if(currentPass.trim().length() > 0 && confirmPasswordInput.getHelperText().toString().equals("* Password are the same")){
            changeView.setEnabled(true);
        }
        else {
            changeView.setEnabled(false);
        }
    }

    private void passwordProcess(){
        newPasswordInput.getEditText().addTextChangedListener(passwordWatcher);
        confirmPasswordInput.getEditText().addTextChangedListener(passwordWatcher);
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
            if(newPasswordInput.getEditText().getText().length() > 5){
                if(confirmPasswordInput.getEditText().getText().length() > 5){
                    if(confirmPasswordInput.getEditText().getText().toString().equals(newPasswordInput.getEditText().getText().toString())){
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        confirmPasswordInput.setHelperText("* Password are the same");
                        confirmPasswordInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                    }else {
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_fail_icon));
                        confirmPasswordInput.setHelperText("* Password are different");
                        confirmPasswordInput.setHelperTextColor(getColorStateList(R.color.colorFail));
                    }
                }else {
                    //passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmPasswordInput.setHelperText(null);
                }

                if(newPasswordInput.getEditText().getText().length() > 10){
                    newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                    newPasswordInput.setHelperText("* Password is good");
                    newPasswordInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                }
                else if(newPasswordInput.getEditText().getText().length() > 5){
                    newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_warning_icon));
                    newPasswordInput.setHelperText("* Password is medium");
                    newPasswordInput.setHelperTextColor(getColorStateList(R.color.colorWarning));
                }
                else {
                    newPasswordInput.setHelperText(null);
                }

            }else {
                newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                newPasswordInput.setHelperText(null);
                confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                confirmPasswordInput.setHelperText(null);
            }
            checkInput();
        }
    };

    private void buildView(){
        currentPasswordInput = findViewById(R.id.current_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        currentImage = findViewById(R.id.current_password_image);
        newPassImage = findViewById(R.id.new_password_image);
        confirmImage = findViewById(R.id.confirm_password_image);
        changeView = findViewById(R.id.security_change_btn);
    }

    private void toolsManagement(){
        Environment.toolbarProcess(this, R.id.security_toolbar);
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
            case R.id.forgot_password_btn:
                Intent forgotIntent = new Intent(getApplicationContext(), ForgotActivity.class);
                startActivity(forgotIntent);
                break;
            case R.id.security_cancel_btn:
                super.onBackPressed();
                break;
        }
    }
}

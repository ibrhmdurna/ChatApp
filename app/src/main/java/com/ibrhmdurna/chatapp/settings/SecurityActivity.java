package com.ibrhmdurna.chatapp.settings;

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

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.AppController;

import java.util.Objects;

public class SecurityActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private ImageView newPassImage;
    private ImageView confirmImage;
    private TextView changeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        toolsManagement();
    }

    private void checkInput(){
        String currentPass = Objects.requireNonNull(currentPasswordInput.getEditText()).getText().toString();

        if(currentPass.trim().length() > 0 && confirmPasswordInput.getHelperText() != null && confirmPasswordInput.getHelperText().toString().equals(getString(R.string.password_are_the_same))){
            changeView.setEnabled(true);
        }
        else {
            changeView.setEnabled(false);
        }
    }

    private void passwordProcess(){
        Objects.requireNonNull(currentPasswordInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentPasswordInput.setError(null);
            }
        });
        Objects.requireNonNull(newPasswordInput.getEditText()).addTextChangedListener(passwordWatcher);
        Objects.requireNonNull(confirmPasswordInput.getEditText()).addTextChangedListener(passwordWatcher);
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
            if(Objects.requireNonNull(newPasswordInput.getEditText()).getText().length() > 5){
                if(Objects.requireNonNull(confirmPasswordInput.getEditText()).getText().length() > 5){
                    if(confirmPasswordInput.getEditText().getText().toString().equals(newPasswordInput.getEditText().getText().toString())){
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        confirmPasswordInput.setHelperText(getString(R.string.password_are_the_same));
                        confirmPasswordInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                    }else {
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_fail_icon));
                        confirmPasswordInput.setHelperText(getString(R.string.password_are_different));
                        confirmPasswordInput.setHelperTextColor(getColorStateList(R.color.colorFail));
                    }
                }else {
                    //passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmPasswordInput.setHelperText(null);
                }

                if(newPasswordInput.getEditText().getText().length() > 10){
                    newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                    newPasswordInput.setHelperText(getString(R.string.password_is_good));
                    newPasswordInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                }
                else if(newPasswordInput.getEditText().getText().length() > 5){
                    newPassImage.setImageDrawable(getDrawable(R.drawable.ic_password_warning_icon));
                    newPasswordInput.setHelperText(getString(R.string.password_is_medium));
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
            currentPasswordInput.setError(null);
            checkInput();
        }
    };

    @Override
    public void buildView(){
        currentPasswordInput = findViewById(R.id.current_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        newPassImage = findViewById(R.id.new_password_image);
        confirmImage = findViewById(R.id.confirm_password_image);
        changeView = findViewById(R.id.security_change_btn);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.security_toolbar);
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
            case R.id.security_change_btn:
                AppController.getInstance().closeKeyboard(this);
                Update.getInstance().updatePassword(this, currentPasswordInput, newPasswordInput, confirmPasswordInput);
                break;
        }
    }
}

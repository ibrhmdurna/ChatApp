package com.ibrhmdurna.chatapp.start;

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
import com.ibrhmdurna.chatapp.database.Search;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.Environment;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout emailInput, passwordInput, confirmInput;
    private TextView nextView;
    private ImageView passwordImage, confirmImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolsManagement();
    }

    private void checkInput(){
        String email = Objects.requireNonNull(emailInput.getEditText()).getText().toString();

        if(email.trim().length() > 0 && Objects.requireNonNull(confirmInput.getHelperText()).toString().equals(getString(R.string.password_are_the_same))){
            nextView.setEnabled(true);
        }
        else {
            nextView.setEnabled(false);
        }
    }

    private void passwordProcess(){
        Objects.requireNonNull(emailInput.getEditText()).addTextChangedListener(inputWatcher);
        Objects.requireNonNull(passwordInput.getEditText()).addTextChangedListener(inputWatcher);
        Objects.requireNonNull(confirmInput.getEditText()).addTextChangedListener(inputWatcher);
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
            if(Objects.requireNonNull(passwordInput.getEditText()).getText().length() > 5){
                if(Objects.requireNonNull(confirmInput.getEditText()).getText().length() > 5){
                    if(confirmInput.getEditText().getText().toString().equals(passwordInput.getEditText().getText().toString())){
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                        confirmInput.setHelperText(getString(R.string.password_are_the_same));
                        confirmInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                    }else {
                        confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_fail_icon));
                        confirmInput.setHelperText(getString(R.string.password_are_different));
                        confirmInput.setHelperTextColor(getColorStateList(R.color.colorFail));
                    }
                }else {
                    //passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                    confirmInput.setHelperText(getString(R.string.password_must_be_at_least_6_characters));
                    confirmInput.setHelperTextColor(getColorStateList(android.R.color.tab_indicator_text));
                }

                if(passwordInput.getEditText().getText().length() > 10){
                    passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_done_icon));
                    passwordInput.setHelperText(getString(R.string.password_is_good));
                    passwordInput.setHelperTextColor(getColorStateList(android.R.color.holo_green_light));
                }
                else if(passwordInput.getEditText().getText().length() > 5){
                    passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_warning_icon));
                    passwordInput.setHelperText(getString(R.string.password_is_medium));
                    passwordInput.setHelperTextColor(getColorStateList(R.color.colorWarning));
                }
                else {
                    passwordInput.setHelperText(null);
                }
            }else {
                passwordImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                passwordInput.setHelperText(null);
                confirmImage.setImageDrawable(getDrawable(R.drawable.ic_password_icon));
                confirmInput.setHelperText(getString(R.string.password_must_be_at_least_6_characters));
                confirmInput.setHelperTextColor(getColorStateList(android.R.color.tab_indicator_text));
            }
            checkInput();
        }
    };

    @Override
    public void buildView(){
        emailInput = findViewById(R.id.reg_email_input);
        passwordInput = findViewById(R.id.reg_password_input);
        confirmInput = findViewById(R.id.reg_confirm_input);
        nextView = findViewById(R.id.register_next_btn);
        passwordImage = findViewById(R.id.reg_password_image);
        confirmImage = findViewById(R.id.reg_confirm_image);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.register_toolbar);
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
                Search.getInstance().checkEmail(this, emailInput, passwordInput);
                break;
        }
    }
}

package com.ibrhmdurna.chatapp.settings;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.AppController;

import java.util.Objects;

public class ForgotActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout emailInput;
    private Button sendBtn;
    private LinearLayout sendLayout;
    private LottieAnimationView doneAnim;
    private TextView successText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        toolsManagement();
    }

    private void inputProcess(){
        Objects.requireNonNull(emailInput.getEditText()).addTextChangedListener(inputWatcher);
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
            checkUpdate();
        }
    };

    private void checkUpdate(){
        if(Objects.requireNonNull(emailInput.getEditText()).getText().length() > 0)
            sendBtn.setEnabled(true);
        else
            sendBtn.setEnabled(false);
    }

    @Override
    public void buildView() {
        emailInput = findViewById(R.id.forgot_email_input);
        sendBtn = findViewById(R.id.send_btn);
        sendLayout = findViewById(R.id.send_layout);
        doneAnim = findViewById(R.id.done_anim_view);
        successText = findViewById(R.id.success_text);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this);
        buildView();
        inputProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_btn:
                AppController.getInstance().closeKeyboard(this);
                Update.getInstance().resetPassword(this, emailInput, sendLayout ,doneAnim, successText);
                break;
        }
    }
}

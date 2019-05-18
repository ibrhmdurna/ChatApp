package com.ibrhmdurna.chatapp.settings;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.AppController;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.util.Objects;

public class DeleteAccountActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextInputLayout passwordInput;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        toolsManagement();
    }

    private void deleteProcess(){
        final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, getString(R.string.delete_dialog_content), getString(R.string.cancel), getString(R.string._delete));
        TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
        assert positiveBtn != null;
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Delete.getInstance().deleteAccount(DeleteAccountActivity.this, passwordInput);
            }
        });
    }

    private void inputProcess(){
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
            if(Objects.requireNonNull(passwordInput.getEditText()).getText().toString().trim().length() > 0){
                deleteBtn.setEnabled(true);
            }
            else{
                deleteBtn.setEnabled(false);
            }
        }
    };

    @Override
    public void buildView() {
        passwordInput = findViewById(R.id.password_input);
        deleteBtn = findViewById(R.id.delete_account_btn);
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
            case R.id.delete_account_btn:
                AppController.getInstance().closeKeyboard(this);
                deleteProcess();
                break;
        }
    }
}

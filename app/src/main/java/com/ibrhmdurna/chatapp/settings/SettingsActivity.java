package com.ibrhmdurna.chatapp.settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.start.StartActivity;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

public class SettingsActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolsManagement();
    }

    private void sendToStart(){
        Intent startIntent = new Intent(SettingsActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.settingsToolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_account_item:
                Intent editAccountIntent = new Intent(getApplicationContext(), EditAccountActivity.class);
                startActivity(editAccountIntent);
                break;
            case R.id.security_item:
                Intent securityIntent = new Intent(getApplicationContext(), SecurityActivity.class);
                startActivity(securityIntent);
                break;
            case R.id.blocked_accounts_item:
                Intent blockedIntent = new Intent(getApplicationContext(), BlockedAccountsActivity.class);
                startActivity(blockedIntent);
                break;
            case R.id.chat_settings_item:
                Intent chatSettingsIntent = new Intent(getApplicationContext(), ChatSettingsActivity.class);
                startActivity(chatSettingsIntent);
                break;
            case R.id.history_item:
                Intent historyIntent = new Intent(getApplicationContext(), ThemesActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.delete_account_item:
                Intent deleteIntent = new Intent(getApplicationContext(), DeleteAccountActivity.class);
                startActivity(deleteIntent);
                break;
            case R.id.about_item:
                Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.logout_item:
                final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, "Log out of ChatApp?", "Cancel", "Logout");
                TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Delete.getInstance().deviceToken(FirebaseAuth.getInstance().getUid());
                        FirebaseAuth.getInstance().signOut();
                        sendToStart();
                    }
                });
                break;
        }
    }
}

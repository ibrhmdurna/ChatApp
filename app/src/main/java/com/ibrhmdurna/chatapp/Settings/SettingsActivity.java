package com.ibrhmdurna.chatapp.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolsManagement();
    }

    private void toolsManagement(){
        Environment.toolbarProcess(this, R.id.settingsToolbar);
    }

    private void logoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        App.Theme.getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView content = view.findViewById(R.id.dialog_content_text);
        content.setText("Log out of ChatApp?");

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setText("Cancel");
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText("Logout");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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
                logoutDialog();
                break;
        }
    }
}

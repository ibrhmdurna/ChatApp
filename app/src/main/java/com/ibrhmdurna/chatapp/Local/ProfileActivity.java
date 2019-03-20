package com.ibrhmdurna.chatapp.Local;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.MoreBottomSheetDialog;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, MoreBottomSheetDialog.BottomSheetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_back_view:
                super.onBackPressed();
                break;
            case R.id.profile_options_view:
                MoreBottomSheetDialog moreBottomSheetDialog = new MoreBottomSheetDialog(true);
                moreBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "share":
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello, I Name Surname (@email) would you like to join us? ChatApp Inc.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                break;
        }
    }
}

package com.ibrhmdurna.chatapp.Application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ibrhmdurna.chatapp.Local.ProfileActivity;
import com.ibrhmdurna.chatapp.Main.MainActivity;
import com.ibrhmdurna.chatapp.Start.StartActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startIntent = new Intent(SplashActivity.this, StartActivity.class);
        startActivity(startIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}

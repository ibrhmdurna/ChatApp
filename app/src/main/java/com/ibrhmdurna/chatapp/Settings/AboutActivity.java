package com.ibrhmdurna.chatapp.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;

public class AboutActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolsManagement();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void getAppVersion(){
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionView.setText(version + " version");
    }

    @Override
    public void buildView(){
        versionView = findViewById(R.id.version_view);
    }

    @Override
    public void toolsManagement(){
        Environment.toolbarProcess(this, R.id.about_toolbar);
        buildView();
        getAppVersion();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.info_view:
                Intent infoIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                infoIntent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(infoIntent);
                break;
        }
    }
}

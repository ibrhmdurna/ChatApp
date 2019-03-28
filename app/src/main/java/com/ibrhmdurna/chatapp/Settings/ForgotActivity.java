package com.ibrhmdurna.chatapp.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Util.Environment;

public class ForgotActivity extends AppCompatActivity implements ViewComponentFactory {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        toolsManagement();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.forgot_toolbar);
    }
}

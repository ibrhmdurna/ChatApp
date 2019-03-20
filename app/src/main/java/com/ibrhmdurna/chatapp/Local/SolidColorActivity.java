package com.ibrhmdurna.chatapp.Local;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.ColorAdapter;
import com.ibrhmdurna.chatapp.Utils.Environment;

public class SolidColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solid_color);

        toolsManagement();
    }

    private void colorProcess(){
        RecyclerView colorView = findViewById(R.id.color_container);
        ColorAdapter colorAdapter = new ColorAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        colorView.setLayoutManager(gridLayoutManager);
        colorView.setHasFixedSize(true);
        colorView.setAdapter(colorAdapter);
    }

    private void toolsManagement() {
        Environment.toolbarProcess(this, R.id.solid_color_toolbar);
        colorProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }
}

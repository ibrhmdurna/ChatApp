package com.ibrhmdurna.chatapp.local;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.adapter.ColorAdapter;
import com.ibrhmdurna.chatapp.util.Environment;

public class SolidColorActivity extends AppCompatActivity implements ViewComponentFactory {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
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

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcess(this);
        colorProcess();
    }
}

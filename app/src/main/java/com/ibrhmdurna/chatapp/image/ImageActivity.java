package com.ibrhmdurna.chatapp.image;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;

import java.util.Objects;

public class ImageActivity extends AppCompatActivity implements ViewComponentFactory {

    private TextView nameSurnameText;
    private PhotoView photoView;

    private String nameSurname;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        nameSurname = getIntent().getStringExtra("nameSurname");
        image = getIntent().getStringExtra("image");

        toolsManagement();
    }

    private void getContent(){
        nameSurnameText.setText(nameSurname);

        try {
            Glide.with(this).load(image).into(photoView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void toolbarProcess(){
        Toolbar toolbar = findViewById(R.id.photo_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon_2);
    }

    @Override
    public void toolsManagement() {
        toolbarProcess();
        buildView();
        getContent();
    }

    @Override
    public void buildView() {
        nameSurnameText = findViewById(R.id.photo_name_surname);
        photoView = findViewById(R.id.photo_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status.getInstance().onConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status.getInstance().onDisconnect();
    }
}

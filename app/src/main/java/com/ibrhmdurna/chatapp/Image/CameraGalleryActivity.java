package com.ibrhmdurna.chatapp.Image;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.AlbumAdapter;
import com.ibrhmdurna.chatapp.Utils.Environment;
import com.ibrhmdurna.chatapp.Utils.FileProcess;

import java.util.List;

public class CameraGalleryActivity extends AppCompatActivity {

    private RecyclerView galleryContainer;
    private TextView subTitle;

    private String isContext = "Share";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);

        isContext = getIntent().getStringExtra("isContext");

        toolsManagement();
    }

    private void getAlbumPhoto(final List<String> list){
        galleryContainer.removeAllViews();
        final AlbumAdapter albumAdapter = new AlbumAdapter(this, list, isContext);
        galleryContainer.setAdapter(albumAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        galleryContainer.setLayoutManager(gridLayoutManager);

        galleryContainer.setHasFixedSize(true);
        galleryContainer.setItemViewCacheSize(100);
        galleryContainer.setDrawingCacheEnabled(true);
        galleryContainer.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    @SuppressLint("SetTextI18n")
    private void galleyProcess(){

        getAlbumPhoto(FileProcess.getAllGalleryPhoto(this));

        if(FileProcess.getAllGalleryPhoto(this).size() > 1){
            subTitle.setText(FileProcess.getAllGalleryPhoto(this).size() + " photos");
        }
        else {
            subTitle.setText(FileProcess.getAllGalleryPhoto(this).size() + " photo");
        }
    }

    private void buildView(){
        galleryContainer = findViewById(R.id.c_gallery_container);
        subTitle = findViewById(R.id.c_gallery_subtitle);
    }

    private void toolsManagement() {
        Environment.toolbarProcess(this, R.id.c_gallery_toolbar);
        buildView();
        galleyProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }
}

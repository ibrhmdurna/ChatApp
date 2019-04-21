package com.ibrhmdurna.chatapp.image;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.adapter.AlbumAdapter;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.FileController;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class CameraGalleryActivity extends AppCompatActivity implements ViewComponentFactory {

    private RecyclerView galleryContainer;
    private TextView subTitle;

    private String isContext = "Share";
    private boolean isRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);

        isContext = getIntent().getStringExtra("isContext");
        isRegister = getIntent().getBooleanExtra("isRegister", false);

        toolsManagement();
    }

    private void getAlbumPhoto(final List<String> list){
        galleryContainer.removeAllViews();
        final AlbumAdapter albumAdapter = new AlbumAdapter(this, list, isContext, isRegister);
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

        getAlbumPhoto(FileController.getInstance().getAllGalleryPhoto(this));

        if(FileController.getInstance().getAllGalleryPhoto(this).size() > 1){
            subTitle.setText(FileController.getInstance().getAllGalleryPhoto(this).size() + " photos");
        }
        else {
            subTitle.setText(FileController.getInstance().getAllGalleryPhoto(this).size() + " photo");
        }
    }

    @Override
    public void buildView(){
        galleryContainer = findViewById(R.id.c_gallery_container);
        subTitle = findViewById(R.id.c_gallery_subtitle);
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcess(this, R.id.c_gallery_toolbar);
        buildView();
        galleyProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }
}

package com.ibrhmdurna.chatapp.image;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
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
    private TextView toolbarSubtitle;
    private ImageButton upAttackBtn;

    private String isContext = "Share";
    private boolean isRegister;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);

        isContext = getIntent().getStringExtra("isContext");
        isRegister = getIntent().getBooleanExtra("isRegister", false);
        uid = getIntent().getStringExtra("user_id");

        toolsManagement();
    }

    private void getAlbumPhoto(final List<String> list){
        galleryContainer.removeAllViews();
        final AlbumAdapter albumAdapter = new AlbumAdapter(this, list, isContext, isRegister, uid);
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
            subTitle.setText(FileController.getInstance().getAllGalleryPhoto(this).size() + " " + getString(R.string.photos));
            toolbarSubtitle.setText(FileController.getInstance().getAllGalleryPhoto(this).size() + " " + getString(R.string.photos));
        }
        else {
            subTitle.setText(FileController.getInstance().getAllGalleryPhoto(this).size() + " " + getString(R.string.photo));
            toolbarSubtitle.setText(FileController.getInstance().getAllGalleryPhoto(this).size() + " " + getString(R.string.photo));
        }
    }

    private void scrollListener(){
        galleryContainer.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_down);
                    upAttackBtn.setAnimation(fadeOut);
                    upAttackBtn.setVisibility(View.GONE);
                }
                else if (scrollY > 1){
                    if(upAttackBtn.getVisibility() == View.GONE){
                        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_down);
                        upAttackBtn.setAnimation(fadeIn);
                        upAttackBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        upAttackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryContainer.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void buildView(){
        galleryContainer = findViewById(R.id.c_gallery_container);
        subTitle = findViewById(R.id.c_gallery_subtitle);
        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
        upAttackBtn = findViewById(R.id.up_attack_btn);
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcessSubtitle(this);
        buildView();
        scrollListener();
        galleyProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }
}

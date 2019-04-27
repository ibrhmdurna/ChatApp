package com.ibrhmdurna.chatapp.image;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.isseiaoki.simplecropview.CropImageView;
import com.squareup.picasso.Picasso;

public class CropActivity extends AppCompatActivity implements View.OnClickListener, ViewComponentFactory {

    private CropImageView cropImageView;

    private int position;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        uid = getIntent().getStringExtra("user_id");

        toolsManagement();
    }

    private void imageProcess(){
        cropImageView = findViewById(R.id.crop_image_view);
        position = getIntent().getIntExtra("position", 0);

        if(ImageController.getInstance().getCameraImage() != null){
            cropImageView.setImageBitmap(ImageController.getInstance().getCameraImage());
        }
        else {
            String path = ImageController.getInstance().getPath().get(position);
            UniversalImageLoader.setImage(path, cropImageView, null, "file://");
        }
    }

    private void crop(){
        if(ImageController.getInstance().getCameraImage() != null){
            ImageController.getInstance().setCameraCroppedImage(cropImageView.getCroppedBitmap());
        }
        else {
            ImageController.getInstance().setImage(cropImageView.getCroppedBitmap());
        }

        Intent shareIntent = new Intent(this, ShareActivity.class);
        shareIntent.putExtra("user_id", uid);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareIntent);
        finish();
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }

    @Override
    public void toolsManagement(){
        imageProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.crop_cancel_btn:
                super.onBackPressed();
                break;
            case R.id.crop_btn:
                crop();
                break;
        }
    }
}

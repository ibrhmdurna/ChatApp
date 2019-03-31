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

public class CropActivity extends AppCompatActivity implements View.OnClickListener, ViewComponentFactory {

    private CropImageView cropImageView;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolsManagement();
    }

    private void imageProcess(){
        cropImageView = findViewById(R.id.crop_image_view);
        position = getIntent().getIntExtra("position", 0);

        if(ImageController.getCameraImage() != null){
            cropImageView.setImageBitmap(ImageController.getCameraPath());
        }
        else {
            String path = ImageController.getPath().get(position);
            UniversalImageLoader.setImage(path, cropImageView, null, "file://");
        }
    }

    private void crop(){
        if(ImageController.getCameraImage() != null){
            ImageController.setCameraCroppedImage(cropImageView.getCroppedBitmap());
        }
        else {
            ImageController.setImage(cropImageView.getCroppedBitmap());
        }

        Intent chatIntent = new Intent(this, ShareActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(chatIntent);
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

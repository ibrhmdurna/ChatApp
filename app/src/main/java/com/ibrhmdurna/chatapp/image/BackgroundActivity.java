package com.ibrhmdurna.chatapp.image;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.settings.ChatSettingsActivity;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.isseiaoki.simplecropview.CropImageView;

public class BackgroundActivity extends AppCompatActivity implements View.OnClickListener, ViewComponentFactory {

    private CropImageView cropImageView;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolsManagement();
    }

    private void imageProcess(){
        cropImageView = findViewById(R.id.crop_image_view);
        position = getIntent().getIntExtra("position", 0);

        String path = ImageController.getInstance().getPath().get(position);
        UniversalImageLoader.setImage(path, cropImageView, null, "file://");
    }


    private void crop(){
        ImageController.getInstance().setBackgroundImage(cropImageView.getCroppedBitmap());
        ImageController.getInstance().setBackgroundColor(0);

        Intent chatSettingsIntent = new Intent(this, ChatSettingsActivity.class);
        chatSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(chatSettingsIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_cancel_btn:
                super.onBackPressed();
                break;
            case R.id.back_crop_btn:
                crop();
                break;
        }
    }

    @Override
    public void toolsManagement() {
        imageProcess();
    }

    @Override
    public void buildView() {

    }
}

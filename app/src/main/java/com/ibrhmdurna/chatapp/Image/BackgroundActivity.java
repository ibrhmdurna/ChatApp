package com.ibrhmdurna.chatapp.Image;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Settings.ChatSettingsActivity;
import com.ibrhmdurna.chatapp.Utils.ImageController;
import com.ibrhmdurna.chatapp.Utils.UniversalImageLoader;
import com.isseiaoki.simplecropview.CropImageView;

public class BackgroundActivity extends AppCompatActivity implements View.OnClickListener {

    private CropImageView cropImageView;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolsManagement();
    }

    private void imageProcess(){
        cropImageView = findViewById(R.id.crop_image_view);
        position = getIntent().getIntExtra("position", 0);

        String path = ImageController.getPath().get(position);
        UniversalImageLoader.setImage(path, cropImageView, null, "file://");
    }

    private void toolsManagement(){
        imageProcess();
    }

    private void crop(){
        ImageController.setBackgroundImage(cropImageView.getCroppedBitmap());
        ImageController.setBackgroundColor(0);

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
}

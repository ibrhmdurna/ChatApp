package com.ibrhmdurna.chatapp.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.settings.EditAccountActivity;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.ByteArrayOutputStream;

public class ProfileImageActivity extends AppCompatActivity implements View.OnClickListener, ViewComponentFactory {

    private CropImageView cropImageView;

    private String path;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolsManagement();
    }

    private void changeProfileImage(){

        Bitmap bitmap = cropImageView.getCroppedBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        bitmap.recycle();

        ImageController.setProfileImageBytes(bytes);

        Intent editIntent = new Intent(this, EditAccountActivity.class);
        editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(editIntent);

        ImageController.setCameraImage(null);
        ImageController.setImage(null);
        ImageController.setCameraCroppedImage(null);
    }

    private void imageRotate(){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap image;
        if(ImageController.getCameraImage() != null){
            Bitmap camera = ImageController.getCameraImage();
            image = Bitmap.createBitmap(camera, 0,0, camera.getWidth(), camera.getHeight(), matrix,true);
            ImageController.setCameraImage(image);
        }
        else {
            Drawable drawable = cropImageView.getDrawable();
            image = Bitmap.createBitmap(((BitmapDrawable)drawable).getBitmap(), 0, 0, ((BitmapDrawable)drawable).getBitmap().getWidth(), ((BitmapDrawable)drawable).getBitmap().getHeight(), matrix, true);
        }
        cropImageView.setImageBitmap(image);
    }

    private void imageProcess(){
        if(ImageController.getCameraImage() != null){
            cropImageView.setImageBitmap(ImageController.getCameraImage());
        }
        else {
            if(ImageController.getImage() != null){
                cropImageView.setImageBitmap(ImageController.getImage());
            }
            else {
                if(path == null){
                    position = getIntent().getIntExtra("position", 0);
                    path = ImageController.getPath().get(position);
                }

                UniversalImageLoader.setImage(path, cropImageView, null, "file://");
            }
        }
    }

    @Override
    public void buildView(){
        cropImageView = findViewById(R.id.profile_image_corp_view);
    }

    @Override
    public void toolsManagement(){
        buildView();
        imageProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_btn:
                super.onBackPressed();
                break;
            case R.id.rotate_item_view:
                imageRotate();
                break;
            case R.id.finish_btn:
                changeProfileImage();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageController.setCameraImage(null);
        ImageController.setImage(null);
        ImageController.setCameraCroppedImage(null);
    }
}

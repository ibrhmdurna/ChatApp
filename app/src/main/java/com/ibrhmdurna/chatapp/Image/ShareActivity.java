package com.ibrhmdurna.chatapp.Image;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Local.ChatActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.FileProcess;
import com.ibrhmdurna.chatapp.Utils.ImageController;
import com.ibrhmdurna.chatapp.Utils.UniversalImageLoader;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener, OnEmojiPopupShownListener, OnEmojiPopupDismissListener {

    private ViewGroup rootView;
    private EmojiPopup emojiPopup;
    private EmojiEditText messageInput;
    private ImageView emojiBtn;
    private PhotoView imageView;

    private String path;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        toolsManagement();
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageProcess();
    }

    private void shareProcess(){
        if(ImageController.getCameraCroppedImage() != null){
            FileProcess.insertImage(ImageController.getCameraCroppedImage());
        }
        else if(ImageController.getCameraImage() != null){
            FileProcess.insertImage(ImageController.getCameraImage());
        }
        else if(ImageController.getImage() != null) {
            FileProcess.insertImage(ImageController.getImage());
        }
        else {
            Drawable drawable = imageView.getDrawable();
            FileProcess.insertImage(((BitmapDrawable)drawable).getBitmap());
        }


        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(chatIntent);

        ImageController.setCameraImage(null);
        ImageController.setImage(null);
        ImageController.setCameraCroppedImage(null);
    }

    private void imageRotate(){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap image;
        if(ImageController.getCameraCroppedImage() != null){
            Bitmap cropped = ImageController.getCameraCroppedImage();
            image = Bitmap.createBitmap(cropped, 0,0, cropped.getWidth(), cropped.getHeight(), matrix,true);
            ImageController.setCameraCroppedImage(image);
        }
        else if(ImageController.getCameraImage() != null){
            Bitmap camera = ImageController.getCameraImage();
            image = Bitmap.createBitmap(camera, 0,0, camera.getWidth(), camera.getHeight(), matrix,true);
            ImageController.setCameraImage(image);
        }
        else if(ImageController.getImage() != null){
            Bitmap shareImage = ImageController.getImage();
            image = Bitmap.createBitmap(shareImage, 0,0, shareImage.getWidth(), shareImage.getHeight(), matrix,true);
            ImageController.setImage(image);
        }
        else {
            Drawable drawable = imageView.getDrawable();
            image = Bitmap.createBitmap(((BitmapDrawable)drawable).getBitmap(), 0, 0, ((BitmapDrawable)drawable).getBitmap().getWidth(), ((BitmapDrawable)drawable).getBitmap().getHeight(), matrix, true);
        }

        imageView.setImageBitmap(image);
    }

    private void imageProcess(){

        if(ImageController.getCameraCroppedImage() != null){
            imageView.setImageBitmap(ImageController.getCameraCroppedImage());
        }
        else if(ImageController.getCameraImage() != null)
        {
            imageView.setImageBitmap(ImageController.getCameraImage());
        }
        else {
            if(ImageController.getImage() != null){
                imageView.setImageBitmap(ImageController.getImage());
            }
            else {
                if(path == null){
                    position = getIntent().getIntExtra("position", 0);
                    path = ImageController.getPath().get(position);
                }

                UniversalImageLoader.setImage(path, imageView, null, "file://");
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void emojiProcess() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiBtn.setImageDrawable(getDrawable(R.drawable.ic_emoji_white_icon));
            }
        }).build(messageInput);

        messageInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(emojiPopup.isShowing()){
                    onEmojiPopupDismiss();
                }
                return false;
            }
        });
    }

    private void buildView(){
        rootView = findViewById(R.id.root_view);
        emojiBtn = findViewById(R.id.share_emoji_btn);
        messageInput = findViewById(R.id.share_input);
        imageView = findViewById(R.id.album_image_view);
    }

    private void toolbarProcess(){
        Toolbar toolbar = findViewById(R.id.share_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon_2);
    }

    private void toolsManagement() {
        toolbarProcess();
        buildView();
        emojiProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_emoji_btn:
                if(emojiPopup.isShowing())
                    onEmojiPopupDismiss();
                else
                    onEmojiPopupShown();
                break;
            case R.id.rotate_item_view:
                imageRotate();
                break;
            case R.id.share_button:
                shareProcess();
                break;
            case R.id.image_edit_item_view:
                Intent editIntent = new Intent(getApplicationContext(), CropActivity.class);
                editIntent.putExtra("position", position);
                startActivity(editIntent);
                overridePendingTransition(0,0);
                break;
        }
    }

    @Override
    public void onEmojiPopupDismiss() {
        emojiPopup.dismiss();
        emojiBtn.setImageDrawable(getDrawable(R.drawable.ic_emoji_white_icon));
    }

    @Override
    public void onEmojiPopupShown() {
        emojiPopup.toggle();
        emojiBtn.setImageDrawable(getDrawable(R.drawable.ic_keyboard_white_icon));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageController.setCameraImage(null);
        ImageController.setImage(null);
        ImageController.setCameraCroppedImage(null);
    }
}

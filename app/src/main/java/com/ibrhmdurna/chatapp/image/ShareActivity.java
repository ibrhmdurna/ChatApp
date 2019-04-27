package com.ibrhmdurna.chatapp.image;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.ShareFindInfo;
import com.ibrhmdurna.chatapp.database.message.Image;
import com.ibrhmdurna.chatapp.database.strategy.SendMessage;
import com.ibrhmdurna.chatapp.local.ChatActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.models.File;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.controller.FileController;
import com.ibrhmdurna.chatapp.util.controller.ImageController;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import java.io.ByteArrayOutputStream;

public class ShareActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, OnEmojiPopupShownListener, OnEmojiPopupDismissListener {

    private ViewGroup rootView;
    private EmojiPopup emojiPopup;
    private EmojiEditText messageInput;
    private ImageView emojiBtn;
    private PhotoView imageView;

    private String path;
    private int position;

    private String uid;

    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTransparentTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        uid = getIntent().getStringExtra("user_id");

        toolsManagement();
    }

    private void getContent(){
        AbstractFind find = new Find(new ShareFindInfo(this, uid));
        find.getContent();
    }

    private void sendMessage(){
        SendMessage message = new SendMessage(new Image());
        Message messageObject = new Message(FirebaseAuth.getInstance().getUid(), messageInput.getText().toString(), "Image", null, false, false, false);
        messageObject.setDownload(false);
        messageObject.setBitmap(image);
        message.Send(messageObject, uid);

        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(chatIntent);

        ImageController.getInstance().setCameraImage(null);
        ImageController.getInstance().setImage(null);
        ImageController.getInstance().setCameraCroppedImage(null);
    }

    private void imageRotate(){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        image = null;
        if(ImageController.getInstance().getCameraCroppedImage() != null){
            Bitmap cropped = ImageController.getInstance().getCameraCroppedImage();
            image = Bitmap.createBitmap(cropped, 0,0, cropped.getWidth(), cropped.getHeight(), matrix,true);
            ImageController.getInstance().setCameraCroppedImage(image);
        }
        else if(ImageController.getInstance().getCameraImage() != null){
            Bitmap camera = ImageController.getInstance().getCameraImage();
            image = Bitmap.createBitmap(camera, 0,0, camera.getWidth(), camera.getHeight(), matrix,true);
            ImageController.getInstance().setCameraImage(image);
        }
        else if(ImageController.getInstance().getImage() != null){
            Bitmap shareImage = ImageController.getInstance().getImage();
            image = Bitmap.createBitmap(shareImage, 0,0, shareImage.getWidth(), shareImage.getHeight(), matrix,true);
            ImageController.getInstance().setImage(image);
        }
        else {
            Drawable drawable = imageView.getDrawable();
            image = Bitmap.createBitmap(((BitmapDrawable)drawable).getBitmap(), 0, 0, ((BitmapDrawable)drawable).getBitmap().getWidth(), ((BitmapDrawable)drawable).getBitmap().getHeight(), matrix, true);
        }

        imageView.setImageBitmap(image);
    }

    private void imageProcess(){

        if(ImageController.getInstance().getCameraCroppedImage() != null){
            image = ImageController.getInstance().getCameraCroppedImage();
        }
        else if(ImageController.getInstance().getCameraImage() != null)
        {
            image = ImageController.getInstance().getCameraImage();
        }
        else {
            if(ImageController.getInstance().getImage() != null){
                image = ImageController.getInstance().getImage();
            }
            else {
                if(path == null){
                    position = getIntent().getIntExtra("position", 0);
                    path = ImageController.getInstance().getPath().get(position);
                }

                java.io.File imgFile = new java.io.File(path);
                if(imgFile.exists()){
                    image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }
                else{
                    UniversalImageLoader.setImage(path, imageView, null, "file://");
                }
            }
        }

        imageView.setImageBitmap(image);

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

    private void toolbarProcess(){
        Toolbar toolbar = findViewById(R.id.share_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon_2);
    }

    @Override
    public void buildView(){
        rootView = findViewById(R.id.root_view);
        emojiBtn = findViewById(R.id.share_emoji_btn);
        messageInput = findViewById(R.id.share_input);
        imageView = findViewById(R.id.album_image_view);
    }

    @Override
    public void toolsManagement() {
        toolbarProcess();
        buildView();
        emojiProcess();
        getContent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageProcess();
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
                sendMessage();
                break;
            case R.id.image_edit_item_view:
                Intent editIntent = new Intent(getApplicationContext(), CropActivity.class);
                editIntent.putExtra("user_id", uid);
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
        ImageController.getInstance().setCameraImage(null);
        ImageController.getInstance().setImage(null);
        ImageController.getInstance().setCameraCroppedImage(null);
    }
}

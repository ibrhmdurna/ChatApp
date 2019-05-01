package com.ibrhmdurna.chatapp.local;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.database.Connection;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFindAll;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.bridge.FindAll;
import com.ibrhmdurna.chatapp.database.find.ChatFindInfo;
import com.ibrhmdurna.chatapp.database.findAll.MessageFindAll;
import com.ibrhmdurna.chatapp.database.message.Text;
import com.ibrhmdurna.chatapp.database.strategy.SendMessage;
import com.ibrhmdurna.chatapp.image.CameraActivity;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.dialog.GalleryBottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, OnEmojiPopupShownListener, OnEmojiPopupDismissListener, GalleryBottomSheetDialog.BottomSheetListener {

    private ViewGroup rootView;
    private EmojiPopup emojiPopup;
    private EmojiEditText messageInput;
    private ImageButton emojiBtn;
    private ImageView backgroundView;
    private ImageButton sendBtn;

    private PullRefreshLayout swipeRefreshLayout;

    private String uid;

    private AbstractFindAll findAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uid = getIntent().getStringExtra("user_id");

        permissionProcess();
    }

    private void permissionProcess(){
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    toolsManagement();
                }
                else if(report.isAnyPermissionPermanentlyDenied()){
                    permissionDialog();
                }
                else {
                    ChatActivity.super.onBackPressed();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                finish();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    private void permissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView content = view.findViewById(R.id.dialog_content_text);
        content.setText("You need to provide the necessary permissions to reach this section, please go to the settings and give the necessary permissions.");

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setText("Cancel");
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ChatActivity.super.onBackPressed();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText("Settings!");
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                settingsIntent.setData(uri);
                startActivity(settingsIntent);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void loadMessage(){
        findAll = new FindAll(new MessageFindAll(this, uid));
        findAll.getContent();

        FirebaseDatabase.getInstance().getReference().child("Messages").child(FirebaseAuth.getInstance().getUid()).child(uid).addListenerForSingleValueEvent(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                if(dataSnapshot.getChildrenCount() > 50){
                    swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    findAll.getMore();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            },500);
                        }
                    });
                }
                else{
                    swipeRefreshLayout.setEnabled(false);
                }
            }
            else{
                swipeRefreshLayout.setEnabled(false);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void sendMessage(){
        SendMessage message = new SendMessage(new Text());
        Message messageObject = new Message(FirebaseAuth.getInstance().getUid(), messageInput.getText().toString(), "Text", null, false, false, false);
        message.Send(messageObject, uid);
        messageInput.getText().clear();
    }

    private void getContent(){
        AbstractFind find = new Find(new ChatFindInfo(this, uid));
        find.getContent();
    }

    private void backgroundProcess(){
        SharedPreferences prefs = getSharedPreferences("CHAT", MODE_PRIVATE);

        String backgroundImage = prefs.getString("BACKGROUND_IMAGE", null);
        int backgroundColor = prefs.getInt("BACKGROUND_COLOR", 0);

        if(backgroundImage != null){
            byte[] imageAsBytes = Base64.decode(backgroundImage.getBytes(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            backgroundView.setImageBitmap(bitmap);
        }
        else if(backgroundColor != 0){
            backgroundView.setImageResource(backgroundColor);
        }
    }

    private void inputProcess(){

        sendBtn.setEnabled(false);

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(messageInput.getText().toString().trim().length() > 0) {
                    sendBtn.setEnabled(true);
                    Update.getInstance().typing(uid, true);
                }
                else{
                    sendBtn.setEnabled(false);
                    Update.getInstance().typing(uid, false);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getContent();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void emojiProcess() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiBtn.setImageDrawable(getDrawable(R.drawable.ic_emoji_icon));
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

    @Override
    public void buildView(){
        rootView = findViewById(R.id.root_view);
        emojiBtn = findViewById(R.id.chat_emoji_btn);
        messageInput = findViewById(R.id.message_input);
        backgroundView = findViewById(R.id.chat_background_view);
        sendBtn = findViewById(R.id.send_btn);
        swipeRefreshLayout = findViewById(R.id.chat_swipe_container);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.chat_toolbar);
        buildView();
        emojiProcess();
        backgroundProcess();
        inputProcess();
        loadMessage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_emoji_btn:
                if(emojiPopup.isShowing()){
                    onEmojiPopupDismiss();
                }
                else {
                    onEmojiPopupShown();
                }
                break;
            case R.id.chat_gallery_icon:
                GalleryBottomSheetDialog galleryBottomSheetDialog = new GalleryBottomSheetDialog();
                galleryBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
            case R.id.send_btn:
                sendMessage();
                break;
        }
    }

    @Override
    public void onEmojiPopupDismiss() {
        emojiPopup.dismiss();
        emojiBtn.setImageDrawable(getDrawable(R.drawable.ic_emoji_icon));
    }

    @Override
    public void onEmojiPopupShown() {
        emojiPopup.toggle();
        emojiBtn.setImageDrawable(getDrawable(R.drawable.ic_keyboard_icon));
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "gallery":
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                galleryIntent.putExtra("user_id", uid);
                galleryIntent.putExtra("isContext","Share");
                startActivity(galleryIntent);
                break;
            case "camera":
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.putExtra("user_id", uid);
                cameraIntent.putExtra("isContext","Share");
                startActivity(cameraIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Connection.getInstance().onConnect();
        Update.getInstance().messageSeen(uid, true);
        Update.getInstance().chatSeen(uid, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Connection.getInstance().onDisconnect();
        Update.getInstance().typing(uid, false);
        Update.getInstance().messageSeen(uid, false);
        Update.getInstance().chatSeen(uid, false);
    }

    @Override
    protected void onDestroy() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        findAll.onDestroy();
        FirebaseDatabase.getInstance().getReference().child("Messages").child(FirebaseAuth.getInstance().getUid()).child(uid).removeEventListener(valueEventListener);
        super.onDestroy();
    }
}

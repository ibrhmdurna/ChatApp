package com.ibrhmdurna.chatapp.local;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.ChatFindInfo;
import com.ibrhmdurna.chatapp.database.findAll.MessageFindAll;
import com.ibrhmdurna.chatapp.database.message.Text;
import com.ibrhmdurna.chatapp.database.strategy.MessageStrategy;
import com.ibrhmdurna.chatapp.database.strategy.SendMessage;
import com.ibrhmdurna.chatapp.image.CameraActivity;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.dialog.GalleryBottomSheetDialog;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, OnEmojiPopupShownListener, OnEmojiPopupDismissListener, GalleryBottomSheetDialog.BottomSheetListener {

    private ViewGroup rootView;
    private EmojiPopup emojiPopup;
    private EmojiEditText messageInput;
    private ImageButton emojiBtn;
    private ImageView backgroundView;
    private ImageButton addBtn;
    private ImageButton sendBtn;
    /*private ImageButton voiceBtn;
    private ImageButton voiceSendBtn;
    private LinearLayout recordLayout;
    private ImageView recordClearView;
    private ImageButton playItem;
    private ImageButton pauseItem;
    private SeekBar recordLineView;
    private TextView recordTimestamp;
    private LottieAnimationView recordAnim;*/

    //private String voicePath;
    private String uid;

    private AbstractFind findMessage;
    private AbstractFind find;

    public static String NOTIF_ID;

    /*
    private boolean isSend = false;
    private boolean isShortTime = false;

    private boolean isLongClicked = false;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uid = getIntent().getStringExtra("user_id");
        NOTIF_ID = uid;

        toolsManagement();
    }

    /*
    private void sendVoiceMessage(){
        SendMessage message = new SendMessage(new Voice());
        Message messageObject = new Message(FirebaseAuth.getInstance().getUid(), "", "Voice", null, false, false, false, false);
        messageObject.setDownload(false);
        messageObject.setPath(voicePath);
        message.Send(messageObject, uid);
        isSend = true;

        clearRecord(false);
    }
    */

    private void loadMessage(){
        findMessage = new Find(new MessageFindAll(this, uid));
        findMessage.getContent();
    }

    private void sendMessage(){
        SendMessage message = new SendMessage(new Text());
        Message messageObject = new Message(FirebaseAuth.getInstance().getUid(), Objects.requireNonNull(messageInput.getText()).toString(), "Text", null, false, false, false, false);
        message.send(messageObject, uid);
        messageInput.getText().clear();
    }

    private void getContent(){
        find = new Find(new ChatFindInfo(this, uid));
        find.getContent();
    }

    /*
    @SuppressLint("ClickableViewAccessibility")
    private void voiceProcess(){

        MediaController.Player.getInstance().setComponent(recordTimestamp, recordLineView, playItem, pauseItem, voicePath);

        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            public void onLongPress(MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    startRecord();
                }
                isLongClicked = true;
            }
            @Override
            public boolean onDown(MotionEvent e) {
                isLongClicked = false;
                return true;
            }
        });

        voiceBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if(isLongClicked && event.getAction() == ACTION_UP){
                    stopRecord();
                    String time = recordTimestamp.getText().toString();
                    if(time.equals("00:00")){
                        clearRecord(true);
                        isShortTime = true;
                        Toast.makeText(getApplicationContext(), getString(R.string.short_time), Toast.LENGTH_SHORT).show();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShortTime = false;
                            }
                        }, 100);
                    }
                    else{
                        isShortTime = false;
                    }
                }
                return false;
            }
        });
    }

    private void pauseVoice(){
        MediaController.Player.getInstance().pauseVoice();
    }

    private void resumeVoice(){
        MediaController.Player.getInstance().resumeVoice();
    }

    private void playVoice(){
        try {
            MediaController.Player.getInstance().playVoice();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopRecord(){
        recordClearView.setVisibility(View.VISIBLE);
        recordAnim.pauseAnimation();
        recordAnim.setVisibility(View.GONE);
        voiceBtn.setVisibility(View.GONE);
        voiceSendBtn.setVisibility(View.VISIBLE);
        playItem.setVisibility(View.VISIBLE);
        recordLineView.setVisibility(View.VISIBLE);

        MediaController.Recorder.getInstance().stopRecord();
    }

    private void startRecord(){
        if(checkPermissionFromDevice()){
            recordLayout.setVisibility(View.VISIBLE);
            recordClearView.setVisibility(View.INVISIBLE);
            playItem.setVisibility(View.GONE);
            pauseItem.setVisibility(View.GONE);
            recordLineView.setVisibility(View.GONE);
            emojiBtn.setVisibility(View.GONE);
            messageInput.setVisibility(View.GONE);
            addBtn.setVisibility(View.GONE);
            recordAnim.setVisibility(View.VISIBLE);
            recordAnim.playAnimation();

            try {
                File outputFile = getOutputFile();
                outputFile.getParentFile().mkdirs();
                MediaController.Recorder.getInstance().startRecord(recordTimestamp, voicePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            permissionProcess();
        }
    }

    private void clearRecord(boolean clear){
        recordLayout.setVisibility(View.GONE);
        addBtn.setVisibility(View.VISIBLE);
        messageInput.setVisibility(View.VISIBLE);
        emojiBtn.setVisibility(View.VISIBLE);
        voiceBtn.setVisibility(View.VISIBLE);
        voiceSendBtn.setVisibility(View.GONE);

        MediaController.Recorder.getInstance().clearRecord(clear, voicePath);
    }

    private File getOutputFile(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        voicePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/ChatApp/Sent/AUD_"
                + dateFormat.format(new Date())
                + ".3gp";
        return new File(voicePath);
    }

    private void permissionProcess(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    startRecord();
                }
                else if(report.isAnyPermissionPermanentlyDenied()){
                    permissionDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void permissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView content = view.findViewById(R.id.dialog_content_text);
        content.setText(getString(R.string.permission_content));

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setText(getString(R.string.cancel));
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText(getString(R.string._settings));
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
    */

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
                if(Objects.requireNonNull(messageInput.getText()).toString().trim().length() > 0) {
                    sendBtn.setEnabled(true);
                    //sendBtn.setVisibility(View.VISIBLE);
                    //voiceBtn.setVisibility(View.GONE);
                    Update.getInstance().typing(uid, true);
                }
                else{
                    sendBtn.setEnabled(false);
                    //sendBtn.setVisibility(View.GONE);
                    //voiceBtn.setVisibility(View.VISIBLE);
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
        //voiceBtn = findViewById(R.id.voice_btn);
        //recordLayout = findViewById(R.id.record_layout);
        addBtn = findViewById(R.id.chat_gallery_icon);
        /*recordClearView = findViewById(R.id.record_clear_view);
        recordAnim = findViewById(R.id.record_anim_layout);
        voiceSendBtn = findViewById(R.id.voice_send_btn);
        playItem = findViewById(R.id.record_play_item);
        pauseItem = findViewById(R.id.record_pause_item);
        recordLineView = findViewById(R.id.record_line_view);
        recordTimestamp = findViewById(R.id.record_timestamp);
        */
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this, R.id.chat_toolbar);
        buildView();
        emojiProcess();
        backgroundProcess();
        inputProcess();
        loadMessage();
        //voiceProcess();
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
                /*
            case R.id.voice_btn:
                if(recordLayout.getVisibility() != View.VISIBLE && !isShortTime){
                    Toast.makeText(getApplicationContext(), getString(R.string.tap_and_hold), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.record_clear_view:
                clearRecord(true);
                break;
            case R.id.record_play_item:
                if(MediaController.Player.getInstance().getMediaPlayer() == null){
                    playVoice();
                }else{
                    resumeVoice();
                }
                break;
            case R.id.record_pause_item:
                pauseVoice();
                break;
            case R.id.voice_send_btn:
                sendVoiceMessage();
                break;
                */
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

        Status.getInstance().onConnect();
        Update.getInstance().messageSeen(uid, true);
        Update.getInstance().chatSeen(uid, true);

        NOTIF_ID = uid;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(uid, 2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status.getInstance().onDisconnect();
        Update.getInstance().typing(uid, false);
        Update.getInstance().messageSeen(uid, false);
        Update.getInstance().chatSeen(uid, false);
        NOTIF_ID = null;
    }

    @Override
    protected void onDestroy() {
        if(find != null && findMessage != null){
            findMessage.onDestroy();
            find.onDestroy();
        }
        /*
        if(MediaController.Player.getInstance().getMediaPlayer() != null && voicePath != null){
            clearRecord(!isSend);
        }
        */
        NOTIF_ID = null;

        super.onDestroy();
    }
}

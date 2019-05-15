package com.ibrhmdurna.chatapp.util.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.image.PhotoActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.GetTimeAgo;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.ibrhmdurna.chatapp.util.controller.FileController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private List<Message> messageList;
    private String uid;
    private String chatUid;

    private boolean isDownloaded = false;

    public MessageAdapter(Activity context, List<Message> messageList, String chatUid){
        this.context = context;
        this.messageList = messageList;
        this.chatUid = chatUid;
        uid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i){
            case 0:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_message_layout, viewGroup, false);
                return new TextMyMessageViewHolder(view);
            case 1:
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout, viewGroup, false);
                return new TextMessageViewHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_image_message_layout, viewGroup, false);
                return new ImageMyMessageViewHolder(view2);
            case 3:
                View view3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_message_layout, viewGroup, false);
                return new ImageMessageViewHolder(view3);
            case 4:
                View view4 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_voice_message_layout, viewGroup, false);
                return new VoiceMyMessageViewHolder(view4);
            case 5:
                View view5 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.voice_message_layout, viewGroup, false);
                return new VoiceMessageViewHolder(view5);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        for(int x = 0; x < messageList.size() - 1; x++){

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
            String currentTime = simpleDateFormat.format(new Date(messageList.get(x).getTime()));
            String oldTime = simpleDateFormat.format(new Date(messageList.get(x + 1).getTime()));

            if(currentTime.equals(oldTime) && messageList.get(x).getFrom().equals(messageList.get(x + 1).getFrom())){
                messageList.get(x).setProfileVisibility(false);
                messageList.get(x + 1).setProfileVisibility(true);
            }
            else{
                messageList.get(x).setProfileVisibility(true);
            }
        }

        messageList.get(messageList.size() - 1).setProfileVisibility(true);

        switch (viewHolder.getItemViewType()){
            case 0:
                TextMyMessageViewHolder viewHolder1 = (TextMyMessageViewHolder)viewHolder;
                viewHolder1.setData(messageList.get(i), i);
                break;
            case 1:
                TextMessageViewHolder viewHolder2 = (TextMessageViewHolder)viewHolder;
                viewHolder2.setData(messageList.get(i), i);
                break;
            case 2:
                ImageMyMessageViewHolder viewHolder3 = (ImageMyMessageViewHolder)viewHolder;
                viewHolder3.setData(messageList.get(i), i);
                break;
            case 3:
                ImageMessageViewHolder viewHolder4 = (ImageMessageViewHolder)viewHolder;
                viewHolder4.setData(messageList.get(i), i);
                break;
            case 4:
                VoiceMyMessageViewHolder viewHolder5 = (VoiceMyMessageViewHolder)viewHolder;
                viewHolder5.setDate(messageList.get(i), i);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if(message != null && message.getType().equals("Text")){
            if(message.getFrom().equals(uid))
                return 0;
            else
                return 1;
        }
        else if (message != null && message.getType().equals("Image")){
            if(message.getFrom().equals(uid)){
                return 2;
            }
            else{
                return 3;
            }
        }
        else if (message != null && message.getType().equals("Voice")){
            if(message.getFrom().equals(uid)){
                return 4;
            }
            else{
                return 5;
            }
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class VoiceMessageViewHolder extends RecyclerView.ViewHolder{

        public VoiceMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class VoiceMyMessageViewHolder extends RecyclerView.ViewHolder{

        private TextView messageContent;
        private TextView timeText;
        private ImageView sendIcon;
        private LinearLayout seenLayout;
        private TextView messageTimeText;
        private RelativeLayout rootView;
        private ImageButton playItem;
        private ImageButton pauseItem;
        private RelativeLayout mediaLayout;
        private SeekBar voiceLine;
        private TextView durationText;
        private TextView sizeText;
        private ImageButton downloadBtn;
        private SpinKitView loadingBar;
        private LinearLayout mainLayout;
        private LinearLayout voiceDownloadLayout;

        private MediaPlayer mediaPlayer;

        private long startHTime = 0L;
        private Handler customHandler = new Handler();
        private long timeInMilliseconds = 0L;
        private long timeSwapBuff = 0L;
        private long updatedTime = 0L;

        public VoiceMyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            seenLayout = itemView.findViewById(R.id.message_seen_layout);
            sendIcon = itemView.findViewById(R.id.message_send_icon);
            rootView = itemView.findViewById(R.id.root_view);
            messageTimeText = itemView.findViewById(R.id.message_time_text);
            playItem = itemView.findViewById(R.id.play_item);
            pauseItem = itemView.findViewById(R.id.pause_item);
            mediaLayout = itemView.findViewById(R.id.media_layout);
            voiceLine = itemView.findViewById(R.id.voice_seek_line);
            durationText = itemView.findViewById(R.id.voice_duration_text);
            mainLayout = itemView.findViewById(R.id.main_layout);
            voiceDownloadLayout = itemView.findViewById(R.id.voice_download_layout);
            sizeText = itemView.findViewById(R.id.voice_size_text);
            downloadBtn = itemView.findViewById(R.id.download_item);
            loadingBar = itemView.findViewById(R.id.loadingBar);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void setDate(final Message message, int position){
            if(message.isUnsend()){
                mainLayout.setVisibility(View.GONE);
                messageContent.setVisibility(View.VISIBLE);
                messageContent.setText(context.getString(R.string.this_message_was_deleted));
                messageContent.setTypeface(null, Typeface.ITALIC);
            }
            else{
                mainLayout.setVisibility(View.VISIBLE);
                messageContent.setVisibility(View.GONE);

                if (message.getPath().equals("")){
                    pauseItem.setVisibility(View.GONE);
                    playItem.setVisibility(View.GONE);
                    mediaLayout.setVisibility(View.GONE);
                    voiceDownloadLayout.setVisibility(View.VISIBLE);

                    if(message.getSize() != null){
                        sizeText.setText(getStringSizeLengthFile(message.getSize()));
                    }
                }
                else{
                    playItem.setVisibility(View.VISIBLE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    voiceDownloadLayout.setVisibility(View.GONE);

                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                        File file = new File(message.getPath());
                        if(file.exists()){
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(message.getPath());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            int duration = mediaPlayer.getDuration();
                            int secs = (duration / 1000);
                            int mins = secs / 60;
                            secs = secs % 60;
                            durationText.setText("" + String.format("%02d", mins) + ":"
                                    + String.format("%02d", secs));

                            mediaPlayer = null;

                            playItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(mediaPlayer == null){
                                        playVoice(message.getPath());
                                    }
                                    else{
                                        resumeVoice();
                                    }
                                }
                            });

                            pauseItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pauseVoice();
                                }
                            });
                        }
                        else{
                            pauseItem.setVisibility(View.GONE);
                            playItem.setVisibility(View.GONE);
                            mediaLayout.setVisibility(View.GONE);
                            voiceDownloadLayout.setVisibility(View.VISIBLE);

                            if(message.getSize() != null){
                                sizeText.setText(getStringSizeLengthFile(message.getSize()));
                            }
                        }

                    }
                    else{
                        pauseItem.setVisibility(View.GONE);
                        playItem.setVisibility(View.GONE);
                        mediaLayout.setVisibility(View.GONE);
                        voiceDownloadLayout.setVisibility(View.VISIBLE);

                        if(message.getSize() != null){
                            sizeText.setText(getStringSizeLengthFile(message.getSize()));
                        }
                    }
                }
            }

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogMessage(context, message, chatUid, true);
                    dialog.show();
                    return true;
                }
            });

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogMessage(context, message, chatUid, true);
                    dialog.show();
                    return true;
                }
            });

            sendIcon.setVisibility(View.VISIBLE);

            if(message.isSend()){
                sendIcon.setVisibility(View.GONE);
                if(message.isReceive()){
                    if(position == messageList.size() - 1){
                        Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").addValueEventListener(seenEventListener);
                    }
                    else{
                        Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").removeEventListener(seenEventListener);
                        seenLayout.setVisibility(View.GONE);
                    }
                }
            }
            else{
                Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").removeEventListener(seenEventListener);
                seenLayout.setVisibility(View.GONE);
            }


            if(position > 0){
                Message topMessage = messageList.get(position - 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                messageTimeText.setVisibility(View.VISIBLE);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
                String nTime = mSimpleDateFormat.format(new Date(message.getTime()));
                messageTimeText.setText(nTime);
            }
            else{
                messageTimeText.setVisibility(View.GONE);
            }

            if(position == messageList.size() - 1){
                if(seenLayout.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    rootView.setLayoutParams(lp);
                }
                else{
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 30);
                    rootView.setLayoutParams(lp);
                }
            }
            else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                rootView.setLayoutParams(lp);
            }
        }

        private void resumeVoice(){
            playItem.setVisibility(View.GONE);
            pauseItem.setVisibility(View.VISIBLE);

            mediaPlayer.start();
            customHandler.postDelayed(updateTimerDurationThread, 100);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    pauseItem.setVisibility(View.GONE);
                    playItem.setVisibility(View.VISIBLE);
                    voiceLine.setProgress(0);
                    mediaPlayer.stop();
                    mediaPlayer = null;
                    customHandler.removeCallbacks(updateTimerDurationThread);
                }
            });
        }

        private void pauseVoice(){
            playItem.setVisibility(View.VISIBLE);
            pauseItem.setVisibility(View.GONE);

            if(mediaPlayer != null){
                mediaPlayer.pause();
                customHandler.removeCallbacks(updateTimerDurationThread);
            }
        }

        private void playVoice(String path){
            playItem.setVisibility(View.GONE);
            pauseItem.setVisibility(View.VISIBLE);

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            durationText.setText("00:00");
            mediaPlayer.start();
            startHTime = mediaPlayer.getCurrentPosition();
            voiceLine.setMax(mediaPlayer.getDuration());
            voiceLine.setProgress((int) startHTime);

            customHandler.postDelayed(updateTimerDurationThread, 100);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    pauseItem.setVisibility(View.GONE);
                    playItem.setVisibility(View.VISIBLE);
                    voiceLine.setProgress(0);
                    mediaPlayer.stop();
                    mediaPlayer = null;
                    customHandler.removeCallbacks(updateTimerDurationThread);
                }
            });

            voiceLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser && mediaPlayer != null){
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        private Runnable updateTimerDurationThread = new Runnable() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                updateTimerRun(this);
            }
        };

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        private void updateTimerRun(Runnable runnable){
            if(mediaPlayer != null){
                timeInMilliseconds = mediaPlayer.getCurrentPosition() - startHTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;

                voiceLine.setProgress((int) timeInMilliseconds);

                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                if (durationText != null)
                    durationText.setText("" + String.format("%02d", mins) + ":"
                            + String.format("%02d", secs));

                customHandler.postDelayed(runnable, 0);
            }
        }

        private ValueEventListener seenEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boolean isSeen = (boolean) dataSnapshot.getValue();

                    if(isSeen){
                        seenLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        seenLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private class TextMessageViewHolder extends RecyclerView.ViewHolder{

        private EmojiTextView messageContent;
        private TextView timeText;
        private CircleImageView profileImage;
        private RelativeLayout profileLayout;
        private TextView profileText;
        private TextView messageTimeText;
        private RelativeLayout rootView;

        public TextMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            profileLayout = itemView.findViewById(R.id.message_profile_layout);
            profileImage = itemView.findViewById(R.id.message_profile_image);
            profileText = itemView.findViewById(R.id.message_profile_text);
            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            rootView = itemView.findViewById(R.id.root_view);
            messageTimeText = itemView.findViewById(R.id.message_time_text);
        }

        public void setData(final Message message, int position){

            if(message.isUnsend()){
                messageContent.setText(context.getString(R.string.this_message_was_deleted));
                messageContent.setTypeface(null, Typeface.ITALIC);
            }
            else{
                messageContent.setText(message.getMessage());
                messageContent.setTypeface(Typeface.DEFAULT);
            }

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogMessage(context, message, chatUid, false);
                    dialog.show();
                    return true;
                }
            });

            if(position > 0){
                Message topMessage = messageList.get(position - 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                profileLayout.setVisibility(View.VISIBLE);

                Firebase.getInstance().getDatabaseReference().child("Blocks").child(chatUid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            profileText.setText(null);
                            profileImage.setImageDrawable(context.getDrawable(R.drawable.default_avatar));
                        }
                        else{
                            profileImageProcess(profileImage, profileText);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                messageTimeText.setVisibility(View.VISIBLE);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
                String nTime = mSimpleDateFormat.format(new Date(message.getTime()));
                messageTimeText.setText(nTime);
            }
            else{
                profileLayout.setVisibility(View.INVISIBLE);

                messageTimeText.setVisibility(View.GONE);
            }

            if(position == messageList.size() - 1){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 30);
                rootView.setLayoutParams(lp);
            }
            else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                rootView.setLayoutParams(lp);
            }

        }
    }

    private class TextMyMessageViewHolder extends RecyclerView.ViewHolder{

        private EmojiTextView messageContent;
        private TextView timeText;
        private ImageView sendIcon;
        private LinearLayout seenLayout;
        private TextView messageTimeText;
        private RelativeLayout rootView;

        public TextMyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            seenLayout = itemView.findViewById(R.id.message_seen_layout);
            sendIcon = itemView.findViewById(R.id.message_send_icon);
            rootView = itemView.findViewById(R.id.root_view);
            messageTimeText = itemView.findViewById(R.id.message_time_text);
        }

        public void setData(final Message message, int position){

            if(message.isUnsend()){
                messageContent.setText(context.getString(R.string.this_message_was_deleted));
                messageContent.setTypeface(null, Typeface.ITALIC);
            }
            else{
                messageContent.setText(message.getMessage());
                messageContent.setTypeface(Typeface.DEFAULT);
            }

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogMessage(context, message, chatUid, true);
                    dialog.show();
                    return true;
                }
            });

            sendIcon.setVisibility(View.VISIBLE);

            if(message.isSend()){
                sendIcon.setVisibility(View.GONE);
                if(message.isReceive()){
                    if(position == messageList.size() - 1){
                        Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").addValueEventListener(seenEventListener);
                    }
                    else{
                        Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").removeEventListener(seenEventListener);
                        seenLayout.setVisibility(View.GONE);
                    }
                }
            }
            else{
                Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").removeEventListener(seenEventListener);
                seenLayout.setVisibility(View.GONE);
            }


            if(position > 0){
                Message topMessage = messageList.get(position - 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                messageTimeText.setVisibility(View.VISIBLE);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
                String nTime = mSimpleDateFormat.format(new Date(message.getTime()));
                messageTimeText.setText(nTime);
            }
            else{
                messageTimeText.setVisibility(View.GONE);
            }

            if(position == messageList.size() - 1){
                if(seenLayout.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    rootView.setLayoutParams(lp);
                }
                else{
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 30);
                    rootView.setLayoutParams(lp);
                }
            }
            else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                rootView.setLayoutParams(lp);
            }
        }

        private ValueEventListener seenEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boolean isSeen = (boolean) dataSnapshot.getValue();

                    if(isSeen){
                        seenLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        seenLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private class ImageMyMessageViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageView;
        private EmojiTextView messageContent;
        private TextView timeText;
        private ImageView sendIcon;
        private LinearLayout seenLayout;
        private RelativeLayout rootView;
        private TextView imageSizeText;
        private LinearLayout downloadLayout;
        private TextView messageTimeText;
        private SpinKitView loadingBar;
        private RelativeLayout imageLayout;

        public ImageMyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imageView = itemView.findViewById(R.id.message_image);
            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            seenLayout = itemView.findViewById(R.id.message_seen_layout);
            sendIcon = itemView.findViewById(R.id.message_send_icon);
            rootView = itemView.findViewById(R.id.root_view);
            imageSizeText = itemView.findViewById(R.id.image_size_text);
            downloadLayout = itemView.findViewById(R.id.image_download_layout);
            loadingBar = itemView.findViewById(R.id.downloading_progress);
            messageTimeText = itemView.findViewById(R.id.message_time_text);
            imageLayout = itemView.findViewById(R.id.image_layout);
        }

        public void setData(final Message message, int position){

            if(message.isUnsend()){
                messageContent.setVisibility(View.VISIBLE);
                messageContent.setText(context.getString(R.string.this_message_was_deleted));
                messageContent.setTypeface(null, Typeface.ITALIC);
                imageLayout.setVisibility(View.GONE);
            }
            else{
                messageContent.setTypeface(Typeface.DEFAULT);
                imageLayout.setVisibility(View.VISIBLE);

                if(message.getMessage().equals("")){
                    messageContent.setVisibility(View.GONE);
                }
                else{
                    messageContent.setVisibility(View.VISIBLE);
                    messageContent.setText(message.getMessage());
                }

                downloadLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadLayout.setVisibility(View.GONE);
                        myPermissionProcess(message, loadingBar, imageView, downloadLayout);
                    }
                });

                if(message.isReceive()){
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog dialog = DialogController.getInstance().dialogImageMessage(context, message, chatUid, true);
                            dialog.show();
                            return true;
                        }
                    });
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, imageView, "messagePhoto");
                        Intent photoIntent = new Intent(context, PhotoActivity.class);
                        photoIntent.putExtra("time", message.getTime());
                        photoIntent.putExtra("path", message.getPath());
                        photoIntent.putExtra("content", message.getMessage());
                        context.startActivity(photoIntent, options.toBundle());
                    }
                });

                imageView.setImageDrawable(context.getDrawable(R.drawable.ic_photo_default_background));
                loadingBar.setIndeterminate(false);
                loadingBar.setVisibility(View.GONE);
                imageView.setEnabled(false);

                if(message.getPath().equals("")){

                    if(message.getBitmap() != null){
                        imageView.setImageBitmap(message.getBitmap());
                        imageView.setEnabled(true);
                    }
                    else{
                        if(message.getSize() != null){
                            imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                        }

                        downloadLayout.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        File imgFile = new File(message.getPath());

                        if(imgFile.exists()){
                            UniversalImageLoader.setImage(message.getPath(), imageView, null, "file://");
                            downloadLayout.setVisibility(View.GONE);
                            imageView.setEnabled(true);
                        }
                        else{
                            if(message.getSize() != null){
                                imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                            }

                            downloadLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        downloadLayout.setVisibility(View.VISIBLE);

                        if(message.getSize() != null){
                            imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                        }
                    }
                }
            }

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogImageMessage(context, message, chatUid, true);
                    dialog.show();
                    return true;
                }
            });

            sendIcon.setVisibility(View.VISIBLE);

            if(message.isSend()){
                sendIcon.setVisibility(View.GONE);
                if(message.isReceive()){
                    if(position == messageList.size() - 1){
                        Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").addValueEventListener(seenEventListener);
                    }
                    else{
                        Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").removeEventListener(seenEventListener);
                        seenLayout.setVisibility(View.GONE);
                    }
                }
            }
            else{
                Firebase.getInstance().getDatabaseReference().child("Chats").child(chatUid).child(uid).child("seen").removeEventListener(seenEventListener);
                seenLayout.setVisibility(View.GONE);
            }

            if(position > 0){
                Message topMessage = messageList.get(position - 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                messageTimeText.setVisibility(View.VISIBLE);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
                String nTime = mSimpleDateFormat.format(new Date(message.getTime()));
                messageTimeText.setText(nTime);
            }
            else{
                messageTimeText.setVisibility(View.GONE);
            }

            if(position == messageList.size() - 1){
                if(seenLayout.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    rootView.setLayoutParams(lp);
                }
                else{
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 30);
                    rootView.setLayoutParams(lp);
                }
            }
            else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                rootView.setLayoutParams(lp);
            }

        }

        private ValueEventListener seenEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boolean isSeen = (boolean) dataSnapshot.getValue();

                    if(isSeen){
                        seenLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        seenLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private class ImageMessageViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageView;
        private EmojiTextView messageContent;
        private TextView timeText;
        private CircleImageView profileImage;
        private RelativeLayout profileLayout;
        private TextView profileText;
        private RelativeLayout rootView;
        private TextView imageSizeText;
        private LinearLayout downloadLayout;
        private SpinKitView loadingBar;
        private TextView messageTimeText;
        private RelativeLayout imageLayout;

        public ImageMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imageView = itemView.findViewById(R.id.message_image);
            profileLayout = itemView.findViewById(R.id.message_profile_layout);
            profileImage = itemView.findViewById(R.id.message_profile_image);
            profileText = itemView.findViewById(R.id.message_profile_text);
            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            rootView = itemView.findViewById(R.id.root_view);
            imageSizeText = itemView.findViewById(R.id.image_size_text);
            downloadLayout = itemView.findViewById(R.id.image_download_layout);
            loadingBar = itemView.findViewById(R.id.downloading_progress);
            messageTimeText = itemView.findViewById(R.id.message_time_text);
            imageLayout = itemView.findViewById(R.id.image_layout);
        }

        public void setData(final Message message, int position){

            if(message.isUnsend()){
                messageContent.setVisibility(View.VISIBLE);
                messageContent.setText(context.getString(R.string.this_message_was_deleted));
                messageContent.setTypeface(null, Typeface.ITALIC);
                imageLayout.setVisibility(View.GONE);
            }
            else {
                messageContent.setTypeface(Typeface.DEFAULT);
                imageLayout.setVisibility(View.VISIBLE);

                if(message.getMessage().equals("")){
                    messageContent.setVisibility(View.GONE);
                }
                else{
                    messageContent.setVisibility(View.VISIBLE);
                    messageContent.setText(message.getMessage());
                }

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog dialog = DialogController.getInstance().dialogImageMessage(context, message, chatUid, false);
                        dialog.show();
                        return true;
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, imageView, "messagePhoto");
                        Intent photoIntent = new Intent(context, PhotoActivity.class);
                        photoIntent.putExtra("time", message.getTime());
                        photoIntent.putExtra("user_id", chatUid);
                        photoIntent.putExtra("content", message.getMessage());
                        photoIntent.putExtra("path", message.getPath());
                        context.startActivity(photoIntent, options.toBundle());
                    }
                });

                imageView.setImageDrawable(context.getDrawable(R.drawable.ic_photo_accent_default_background));
                loadingBar.setIndeterminate(false);
                loadingBar.setVisibility(View.GONE);
                imageView.setEnabled(false);

                if(message.getPath().equals("")){

                    if(message.getSize() != null){
                        imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                    }

                    if(!message.isDownload()){
                        downloadLayout.setVisibility(View.GONE);
                        loadingBar.setIndeterminate(true);
                        loadingBar.setVisibility(View.VISIBLE);
                        permissionProcess(message, loadingBar, imageView, downloadLayout);
                    }
                    else{
                        downloadLayout.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        File imgFile = new File(message.getPath());

                        if(imgFile.exists()){
                            UniversalImageLoader.setImage(message.getPath(), imageView, null, "file://");
                            downloadLayout.setVisibility(View.GONE);
                            imageView.setEnabled(true);
                        }
                        else{
                            if(message.getSize() != null){
                                imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                            }

                            downloadLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        downloadLayout.setVisibility(View.VISIBLE);

                        if(message.getSize() != null){
                            imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                        }
                    }
                }
            }

            downloadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadLayout.setVisibility(View.GONE);
                    permissionProcess(message, loadingBar, imageView, downloadLayout);

                }
            });

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogImageMessage(context, message, chatUid, false);
                    dialog.show();
                    return true;
                }
            });


            if(position > 0){
                Message topMessage = messageList.get(position - 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(context, message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                profileLayout.setVisibility(View.VISIBLE);
                Firebase.getInstance().getDatabaseReference().child("Blocks").child(chatUid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            profileText.setText(null);
                            profileImage.setImageDrawable(context.getDrawable(R.drawable.default_avatar));
                        }
                        else{
                            profileImageProcess(profileImage, profileText);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                messageTimeText.setVisibility(View.VISIBLE);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");
                String nTime = mSimpleDateFormat.format(new Date(message.getTime()));
                messageTimeText.setText(nTime);
            }
            else{
                profileLayout.setVisibility(View.INVISIBLE);
                messageTimeText.setVisibility(View.GONE);
            }

            if(position == messageList.size() - 1){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 30);
                rootView.setLayoutParams(lp);
            }
            else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                rootView.setLayoutParams(lp);
            }
        }
    }

    private String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if(size < sizeMb)
            return df.format(size / sizeKb)+ " KB";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " GB";

        return "";
    }

    private void profileImageProcess(final CircleImageView profileImage, final TextView profileText){

        Firebase.getInstance().getDatabaseReference().child("Accounts").child(chatUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final Account account = dataSnapshot.getValue(Account.class);

                    if(!profileImage.isSaveEnabled()){
                        assert account != null;
                        if(account.getThumb_image().substring(0,8).equals("default_")){
                            String text = account.getThumb_image().substring(8,9);
                            int index = Integer.parseInt(text);
                            setProfileImage(index, profileImage);
                            String name = account.getName().substring(0,1);
                            profileText.setText(name);
                            profileText.setVisibility(View.VISIBLE);
                        }
                        else {
                            if(context != null){
                                try {
                                    Glide.with(context).load(account.getThumb_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            profileText.setText(null);
                            profileText.setVisibility(View.GONE);
                            profileImage.setSaveEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileImage(int index, CircleImageView profileImage) {
        switch (index){
            case 0:
                profileImage.setImageResource(R.drawable.ic_avatar_0);
                break;
            case 1:
                profileImage.setImageResource(R.drawable.ic_avatar_1);
                break;
            case 2:
                profileImage.setImageResource(R.drawable.ic_avatar_2);
                break;
            case 3:
                profileImage.setImageResource(R.drawable.ic_avatar_3);
                break;
            case 4:
                profileImage.setImageResource(R.drawable.ic_avatar_4);
                break;
            case 5:
                profileImage.setImageResource(R.drawable.ic_avatar_5);
                break;
            case 6:
                profileImage.setImageResource(R.drawable.ic_avatar_6);
                break;
            case 7:
                profileImage.setImageResource(R.drawable.ic_avatar_7);
                break;
            case 8:
                profileImage.setImageResource(R.drawable.ic_avatar_8);
                break;
            case 9:
                profileImage.setImageResource(R.drawable.ic_avatar_9);
                break;
        }
    }

    private void myPermissionVoiceProcess(final Message message, final SpinKitView loadingBar, final LinearLayout downloadLayout, LinearLayout mediaLayout, ImageButton playItem){
        Dexter.withActivity(context)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    if(message.getPath().equals("")){
                        //FileController.getInstance().compressToDownloadImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView);
                    }
                    else{
                        File imgFile = new File(message.getPath());

                        if(imgFile.exists()){
                            //UniversalImageLoader.setImage(message.getPath(), imageView, null, "file://");
                            downloadLayout.setVisibility(View.GONE);
                            //imageView.setEnabled(true);
                        }
                        else{
                            //FileController.getInstance().compressToDownloadImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView);
                        }
                    }
                }
                else if(report.isAnyPermissionPermanentlyDenied()){
                    permissionDialog(downloadLayout);
                }
                else{
                    downloadLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    private void myPermissionProcess(final Message message, final SpinKitView loadingBar, final RoundedImageView imageView, final LinearLayout downloadLayout){
        Dexter.withActivity(context)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    if(message.getPath().equals("")){
                        FileController.getInstance().compressToDownloadImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView);
                    }
                    else{
                        File imgFile = new File(message.getPath());

                        if(imgFile.exists()){
                            UniversalImageLoader.setImage(message.getPath(), imageView, null, "file://");
                            downloadLayout.setVisibility(View.GONE);
                            imageView.setEnabled(true);
                        }
                        else{
                            FileController.getInstance().compressToDownloadImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView);
                        }
                    }
                }
                else if(report.isAnyPermissionPermanentlyDenied()){
                    permissionDialog(downloadLayout);
                }
                else{
                    downloadLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    private void permissionProcess(final Message message, final SpinKitView loadingBar, final RoundedImageView imageView, final LinearLayout downloadLayout){
        Dexter.withActivity(context)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    if(!isDownloaded && message.getPath().equals("")){
                        Log.e("Info ", "Image Downloaded");
                        FileController.getInstance().compressToDownloadAndSaveImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView, MessageAdapter.this);
                        isDownloaded = true;
                    }
                    else{
                        File imgFile = new File(message.getPath());

                        if(imgFile.exists()){
                            UniversalImageLoader.setImage(message.getPath(), imageView, null, "file://");
                            downloadLayout.setVisibility(View.GONE);
                            imageView.setEnabled(true);
                        }
                        else{
                            if(isDownloaded){
                                isDownloaded = false;
                            }
                            else{
                                FileController.getInstance().compressToDownloadAndSaveImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView, MessageAdapter.this);
                            }
                        }
                    }
                }
                else if(report.isAnyPermissionPermanentlyDenied()){
                    permissionDialog(downloadLayout);
                }
                else{
                    downloadLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    private void permissionDialog(final LinearLayout downloadLayout){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        App.Theme.getInstance().getTheme(view.getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView content = view.findViewById(R.id.dialog_content_text);
        content.setText(context.getString(R.string.permission_content));

        TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
        negativeBtn.setText(context.getString(R.string.cancel));
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                downloadLayout.setVisibility(View.VISIBLE);
            }
        });

        TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
        positiveBtn.setText(context.getString(R.string._settings));
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                settingsIntent.setData(uri);
                context.startActivity(settingsIntent);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
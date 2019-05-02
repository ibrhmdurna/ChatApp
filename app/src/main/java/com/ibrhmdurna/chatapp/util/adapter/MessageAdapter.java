package com.ibrhmdurna.chatapp.util.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.image.PhotoActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.GetTimeAgo;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.ibrhmdurna.chatapp.util.controller.FileController;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private List<Message> messageList;
    private String uid;
    private String chatUid;

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
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()){
            case 0:
                TextMyMessageViewHolder viewHolder1 = (TextMyMessageViewHolder)viewHolder;
                viewHolder1.setData(messageList.get(i), i);
                break;
            case 1:
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

                TextMessageViewHolder viewHolder2 = (TextMessageViewHolder)viewHolder;
                viewHolder2.setData(messageList.get(i), i);
                break;
            case 2:
                ImageMyMessageViewHolder viewHolder3 = (ImageMyMessageViewHolder)viewHolder;
                viewHolder3.setData(messageList.get(i), i);
                break;
            case 3:
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

                ImageMessageViewHolder viewHolder4 = (ImageMessageViewHolder)viewHolder;
                viewHolder4.setData(messageList.get(i), i);
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

        return -1;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class TextMessageViewHolder extends RecyclerView.ViewHolder{

        private EmojiTextView messageContent;
        private TextView timeText;
        private CircleImageView profileImage;
        private RelativeLayout profileLayout;
        private TextView profileText;
        private RelativeLayout rootView;

        public TextMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileLayout = itemView.findViewById(R.id.message_profile_layout);
            profileImage = itemView.findViewById(R.id.message_profile_image);
            profileText = itemView.findViewById(R.id.message_profile_text);
            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            rootView = itemView.findViewById(R.id.root_view);
        }

        public void setData(final Message message, int position){

            messageContent.setText(message.getMessage());

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

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                profileLayout.setVisibility(View.VISIBLE);
                profileImageProcess(profileImage, profileText);
            }
            else{
                profileLayout.setVisibility(View.INVISIBLE);
            }

            if(position == messageList.size() - 1){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 40);
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
        private RelativeLayout rootView;

        public TextMyMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            seenLayout = itemView.findViewById(R.id.message_seen_layout);
            sendIcon = itemView.findViewById(R.id.message_send_icon);
            rootView = itemView.findViewById(R.id.root_view);
        }

        public void setData(final Message message, int position){

            messageContent.setText(message.getMessage());

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
                if(message.isReceive()){
                    sendIcon.setVisibility(View.GONE);

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

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                timeText.setText(time);
            }

            if(position == messageList.size() - 1){
                if(seenLayout.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    rootView.setLayoutParams(lp);
                }
                else{
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 40);
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
        private SpinKitView loadingBar;

        public ImageMyMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.message_image);
            messageContent = itemView.findViewById(R.id.message_content);
            timeText = itemView.findViewById(R.id.message_time_view);
            seenLayout = itemView.findViewById(R.id.message_seen_layout);
            sendIcon = itemView.findViewById(R.id.message_send_icon);
            rootView = itemView.findViewById(R.id.root_view);
            imageSizeText = itemView.findViewById(R.id.image_size_text);
            downloadLayout = itemView.findViewById(R.id.image_download_layout);
            loadingBar = itemView.findViewById(R.id.downloading_progress);
        }

        public void setData(final Message message, int position){

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
                    FileController.getInstance().compressToDownloadImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView);
                }
            });

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

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogImageMessage(context, message, chatUid, true);
                    dialog.show();
                    return true;
                }
            });

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogImageMessage(context, message, chatUid, true);
                    dialog.show();
                    return true;
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

            sendIcon.setVisibility(View.VISIBLE);

            if(message.isSend()){
                if(message.isReceive()){
                    sendIcon.setVisibility(View.GONE);

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

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                timeText.setText(time);
            }

            if(position == messageList.size() - 1){
                if(seenLayout.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    rootView.setLayoutParams(lp);
                }
                else{
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 40);
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

        public ImageMessageViewHolder(@NonNull View itemView) {
            super(itemView);

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
        }

        public void setData(final Message message, int position){

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

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
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

            imageView.setImageDrawable(context.getDrawable(R.drawable.ic_photo_default_background));
            loadingBar.setIndeterminate(false);
            loadingBar.setVisibility(View.GONE);
            imageView.setEnabled(false);

            if(message.getPath().equals("")){

                if(message.getSize() != null){
                    imageSizeText.setText(getStringSizeLengthFile(message.getSize()));
                }

                if(!message.isDownload()){
                    FileController.getInstance().compressToDownloadAndSaveImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView, MessageAdapter.this);
                }
                else{
                    downloadLayout.setVisibility(View.VISIBLE);
                }
            }
            else{
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

            downloadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadLayout.setVisibility(View.GONE);
                    FileController.getInstance().compressToDownloadAndSaveImage(message.getUrl(), chatUid, message.getMessage_id(), loadingBar, imageView, MessageAdapter.this);
                }
            });

            if(position > 0){
                Message topMessage = messageList.get(position - 1);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
                String messageTime = simpleDateFormat.format(new Date(message.getTime()));
                String topMessageTime = simpleDateFormat.format(new Date(topMessage.getTime()));

                if(messageTime.equals(topMessageTime)){
                    timeText.setVisibility(View.GONE);
                }
                else{
                    timeText.setVisibility(View.VISIBLE);
                    String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                    timeText.setText(time);
                }
            }
            else{
                timeText.setVisibility(View.VISIBLE);
                String time = GetTimeAgo.getInstance().getMessageAgo(message.getTime());
                timeText.setText(time);
            }

            if(message.isProfileVisibility()){
                profileLayout.setVisibility(View.VISIBLE);
                profileImageProcess(profileImage, profileText);
            }
            else{
                profileLayout.setVisibility(View.INVISIBLE);
            }

            if(position == messageList.size() - 1){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 40);
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
                        if(account.getThumb_image().substring(0,8).equals("default_")){
                            String text = account.getThumb_image().substring(8,9);
                            int index = Integer.parseInt(text);
                            setProfileImage(index, profileImage);
                            String name = account.getName().substring(0,1);
                            profileText.setText(name);
                            profileText.setVisibility(View.VISIBLE);
                        }
                        else {
                            final Picasso picasso = Picasso.get();
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(account.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.default_avatar).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    picasso.load(account.getThumb_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                                }
                            });
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
}

package com.ibrhmdurna.chatapp.database.find;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.database.message.Text;
import com.ibrhmdurna.chatapp.database.strategy.SendMessage;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.models.Message;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BeginChatFindInfo implements IFind {

    private Activity context;

    private CircleImageView profileImage;
    private TextView profileText;
    private TextView nameSurname;
    private TextView accountLocation;
    private TextView friendTime;
    private CircleImageView profileFirst;
    private TextView profileFirstText;
    private CircleImageView profileLast;
    private TextView profileLastText;
    private TextView helloText;
    private TextView helloWaveText;
    private LinearLayout waveLayout;
    private ImageButton waveCloseBtn;
    private TextView waveBtn;
    private EmojiTextView waveEmoji;

    private String chatUid;
    private String uid;

    private LinearLayout beginLayout;

    private int unicode = 0x1F44B;

    public BeginChatFindInfo(Activity context, String chatUid) {
        this.context = context;
        this.chatUid = chatUid;
    }

    @Override
    public void buildView() {
        waveEmoji = context.findViewById(R.id.emoji_text);
        profileImage = context.findViewById(R.id.profile_image);
        profileText = context.findViewById(R.id.profile_text);
        nameSurname = context.findViewById(R.id.account_name_surname);
        accountLocation = context.findViewById(R.id.account_location);
        friendTime = context.findViewById(R.id.friend_time);
        profileFirst = context.findViewById(R.id.profile_image_first);
        profileFirstText = context.findViewById(R.id.profile_text_first);
        profileLast = context.findViewById(R.id.profile_image_last);
        profileLastText = context.findViewById(R.id.profile_text_last);
        helloText = context.findViewById(R.id.hello_text);
        helloWaveText = context.findViewById(R.id.hello_wave_text);
        waveLayout = context.findViewById(R.id.wave_layout);
        waveCloseBtn = context.findViewById(R.id.wave_close_btn);
        waveBtn = context.findViewById(R.id.wave_btn);
        beginLayout = context.findViewById(R.id.chat_begin_layout);
    }

    @Override
    public void getContent() {

        waveEmoji.setText(getEmojiByUnicode(unicode));

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Accounts").child(chatUid).addListenerForSingleValueEvent(contentEventListener);
        assert uid != null;
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).addListenerForSingleValueEvent(accountEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).child(chatUid).addListenerForSingleValueEvent(friendEventListener);
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).child(chatUid).addListenerForSingleValueEvent(chatEventListener);

        waveCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveLayout.setVisibility(View.GONE);
                Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).child(chatUid).child("wave").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            dataSnapshot.getRef().setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        waveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage message = new SendMessage(new Text());
                Message messageObject = new Message(FirebaseAuth.getInstance().getUid(), getEmojiByUnicode(unicode), "Text", null, false, false, false, false);
                message.send(messageObject, chatUid);
            }
        });
    }

    @Override
    public void getMore() {

    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(chatUid).removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).removeEventListener(accountEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).child(chatUid).removeEventListener(friendEventListener);
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).child(chatUid).removeEventListener(chatEventListener);
    }

    private ValueEventListener contentEventListener = new ValueEventListener() {
        @SuppressLint({"SetTextI18n"})
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                beginLayout.setVisibility(View.VISIBLE);
                Account account = dataSnapshot.getValue(Account.class);

                if(account != null){
                    String image = account.getProfile_image();

                    if(image.substring(0, 8).equals("default_")){
                        String value = image.substring(8,9);
                        int index = Integer.parseInt(value);
                        setProfileImage(index, profileImage);
                        setProfileImage(index, profileLast);
                        String name = account.getName().substring(0,1);
                        profileText.setText(name);
                        profileLastText.setText(name);
                        profileText.setVisibility(View.VISIBLE);
                        profileLastText.setVisibility(View.VISIBLE);
                    }
                    else{
                        if(context != null){
                            try {
                                Glide.with(context).load(account.getProfile_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                                Glide.with(context).load(account.getThumb_image()).placeholder(R.drawable.default_avatar).into(profileLast);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        profileText.setText(null);
                        profileText.setVisibility(View.GONE);
                        profileLastText.setText(null);
                        profileLastText.setVisibility(View.GONE);
                    }

                    nameSurname.setText(account.getNameSurname());
                    String language = Locale.getDefault().getLanguage();

                    if(language.equals("tr")){
                        accountLocation.setText(convertLocation(account.getLocation()) + " yaşıyor.");
                        helloWaveText.setText(account.getNameSurname() + " el sallayarak merhaba de.");
                        helloText.setText("ChatApp arkadaşın " + account.getName() + " merhaba de.");
                    }
                    else{
                        accountLocation.setText("Lives in " + convertLocation(account.getLocation()));
                        helloWaveText.setText("Say hi to " + account.getNameSurname() + " with a wave.");
                        helloText.setText("Say hi to your ChatApp friend, " + account.getName());
                    }
                }
            }else{
                beginLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener friendEventListener = new ValueEventListener() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                Friend friend = dataSnapshot.getValue(Friend.class);

                if(friend != null){
                    String language = Locale.getDefault().getLanguage();

                    SimpleDateFormat dateFormat;

                    if(language.equals("tr")){
                        dateFormat = new SimpleDateFormat("d MMMM yyyy HH:mm");
                    }
                    else{
                        dateFormat = new SimpleDateFormat("MMMM d, yyyy HH:mm");
                    }

                    friendTime.setText(dateFormat.format(new Date(friend.getTime())));
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener accountEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                Account account = dataSnapshot.getValue(Account.class);

                if(account != null){
                    String image = account.getProfile_image();

                    if(image.substring(0, 8).equals("default_")){
                        String value = image.substring(8,9);
                        int index = Integer.parseInt(value);
                        setProfileImage(index, profileFirst);
                        String name = account.getName().substring(0,1);
                        profileFirstText.setText(name);
                    }
                    else{
                        if(context != null){
                            try {
                                Glide.with(context).load(account.getThumb_image()).placeholder(R.drawable.default_avatar).into(profileFirst);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        profileFirstText.setText(null);
                        profileFirstText.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener chatEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                final Chat chat = dataSnapshot.getValue(Chat.class);

                if(chat != null){
                    if(chat.isWave()){
                        waveLayout.setVisibility(View.VISIBLE);
                    }else{
                        waveLayout.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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

    private String convertLocation(int location){
        String[] locations = context.getResources().getStringArray(R.array.countries_array);
        return locations[location - 1];
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}

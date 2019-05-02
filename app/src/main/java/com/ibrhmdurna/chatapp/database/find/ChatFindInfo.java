package com.ibrhmdurna.chatapp.database.find;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.util.GetTimeAgo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFindInfo implements IFind {

    private Activity context;

    private CircleImageView profileImage;
    private TextView profileText;
    private TextView nameSurname;
    private TextView lastSeen;
    private TextView typingView;
    private RelativeLayout toolbarView;
    private LinearLayout chatInputLayout;
    private TextView accountNotFoundLayout;

    private String uid;

    private String image;

    public ChatFindInfo(Activity context, String uid) {
        this.context = context;
        this.uid = uid;
        buildView();
    }

    @Override
    public void buildView() {
        profileImage = context.findViewById(R.id.chat_profile_image);
        profileText = context.findViewById(R.id.chat_profile_text);
        nameSurname = context.findViewById(R.id.chat_name_surname);
        lastSeen = context.findViewById(R.id.chat_last_seen);
        typingView = context.findViewById(R.id.chat_typing);
        toolbarView = context.findViewById(R.id.toolbar_view);
        chatInputLayout = context.findViewById(R.id.chat_input_layout);
        accountNotFoundLayout = context.findViewById(R.id.account_not_found_layout);
    }

    @Override
    public void getContent() {
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).addValueEventListener(contentEventListener);
    }

    @Override
    public void getMore() {
        // NOTHING...
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).removeEventListener(contentEventListener);
    }

    private ValueEventListener contentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                final Account account = dataSnapshot.getValue(Account.class);

                if(image == null){
                    image = account.getProfile_image();
                    imageProcess(account);
                }

                if(!image.equals(account.getProfile_image())){
                    image = account.getProfile_image();
                    imageProcess(account);
                }

                nameSurname.setText(account.getNameSurname());

                typingListener(account);

                toolbarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                        profileIntent.putExtra("user_id", uid);
                        context.startActivity(profileIntent);
                    }
                });
            }
            else{
                accountNotFoundLayout.setVisibility(View.VISIBLE);
                chatInputLayout.setVisibility(View.GONE);
                lastSeen.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void typingListener(final Account account){
        final String currentUid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Chats").child(currentUid).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final Chat chat = dataSnapshot.getValue(Chat.class);

                    Firebase.getInstance().getDatabaseReference().child("Friends").child(currentUid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(chat.isTyping()){
                                    typingView.setVisibility(View.VISIBLE);
                                    lastSeen.setVisibility(View.GONE);
                                }
                                else{
                                    onlineListener(account);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if(lastSeen.getVisibility() == View.VISIBLE){

                    }
                }
                else{
                    onlineListener(account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onlineListener(final Account account){
        if(account.isOnline()){
            lastSeen.setVisibility(View.VISIBLE);
            typingView.setVisibility(View.GONE);
            Firebase.getInstance().getDatabaseReference().child("Friends").child(FirebaseAuth.getInstance().getUid()).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        lastSeen.setText("Online");
                    }
                    else {
                        lastSeen.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        else{
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(account.getLast_seen() != null){

                        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).child("online").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final boolean isOnline = (boolean) dataSnapshot.getValue();

                                    if(typingView.getVisibility() != View.VISIBLE){
                                        Firebase.getInstance().getDatabaseReference().child("Friends").child(FirebaseAuth.getInstance().getUid()).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    lastSeen.setVisibility(View.VISIBLE);
                                                    if(isOnline){
                                                        lastSeen.setText("Online");
                                                    }
                                                    else{
                                                        String lastSeenTime = GetTimeAgo.getInstance().getLastSeenAgo(account.getLast_seen());
                                                        lastSeen.setText(lastSeenTime);
                                                    }
                                                }
                                                else{
                                                    lastSeen.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }
                                else{
                                    typingView.setVisibility(View.GONE);
                                    lastSeen.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        typingView.setVisibility(View.GONE);
                        lastSeen.setVisibility(View.GONE);
                    }

                }
            }, 1500);
        }
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

    private void imageProcess(final Account account){
        if(image.substring(0, 8).equals("default_")){
            String value = image.substring(8,9);
            int index = Integer.parseInt(value);
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
        }
    }
}

package com.ibrhmdurna.chatapp.util.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.local.ChatActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.GetTimeAgo;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Fragment context;
    private List<Chat> chatList;

    private String uid;

    public ChatAdapter(Fragment context, List<Chat> chatList){
        this.context = context;
        this.chatList = chatList;
        uid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_layout, viewGroup, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
        Chat chat = chatList.get(i);
        chatViewHolder.setData(chat, i);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView profileText;
        private TextView nameSurname;
        private EmojiTextView lastMessage;
        private TextView time;
        private TextView messageCount;
        private View line;

        private String chatUid;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.chat_profile_image);
            profileText = itemView.findViewById(R.id.chat_profile_text);
            nameSurname = itemView.findViewById(R.id.chat_name_surname_text);
            lastMessage = itemView.findViewById(R.id.chat_content_message);
            time = itemView.findViewById(R.id.chat_time_text);
            messageCount = itemView.findViewById(R.id.chat_message_count);
            line = itemView.findViewById(R.id.chat_line);
        }

        public void setData(final Chat chat, int position){

            chatUid = chat.getChatUid();

            FirebaseDatabase.getInstance().getReference().child("Accounts").child(chatUid).removeEventListener(accountEventListener);
            FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(chatUid).child(chat.getLast_message_id()).removeEventListener(lastMessageEventListener);
            FirebaseDatabase.getInstance().getReference().child("Messages").child(chatUid).child(uid).removeEventListener(messageEventListener);
            FirebaseDatabase.getInstance().getReference().child("Chats").child(uid).child(chatUid).child("typing").removeEventListener(chatEventListener);

            FirebaseDatabase.getInstance().getReference().child("Accounts").child(chatUid).addListenerForSingleValueEvent(accountEventListener);
            FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(chatUid).child(chat.getLast_message_id()).addValueEventListener(lastMessageEventListener);
            FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(chatUid).addValueEventListener(messageEventListener);
            FirebaseDatabase.getInstance().getReference().child("Chats").child(uid).child(chatUid).child("typing").addValueEventListener(chatEventListener);

            String chatTime = GetTimeAgo.getInstance().getChatTimeAgo(chat.getTime());
            time.setText(chatTime);

            if(position == chatList.size() - 1){
                line.setVisibility(View.GONE);
            }
            else{
                line.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(context.getActivity(), ChatActivity.class);
                    chatIntent.putExtra("user_id", chatUid);
                    context.startActivity(chatIntent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog dialog = DialogController.getInstance().dialogChat(context, chatUid);
                    dialog.show();
                    return true;
                }
            });

        }

        private void setProfileImage(final String value, String nameValue){

            if(value.substring(0,8).equals("default_")){
                String text = value.substring(8,9);
                int index = Integer.parseInt(text);
                setProfileImage(index, profileImage);
                String name = nameValue.substring(0,1);
                profileText.setText(name);
                profileText.setVisibility(View.VISIBLE);
            }
            else {
                final Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(true);
                picasso.load(value).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        picasso.load(value).placeholder(R.drawable.default_avatar).into(profileImage);
                    }
                });
                profileText.setText(null);
                profileText.setVisibility(View.GONE);
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

        private ValueEventListener accountEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Account account = dataSnapshot.getValue(Account.class);

                    setProfileImage(account.getThumb_image(), account.getName());
                    nameSurname.setText(account.getNameSurname());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        private ValueEventListener lastMessageEventListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Message message = dataSnapshot.getValue(Message.class);

                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = context.getContext().getTheme();
                    theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true);
                    @ColorInt int color = typedValue.data;
                    lastMessage.setTextColor(color);
                    lastMessage.setTypeface(Typeface.DEFAULT);

                    if(message.getFrom().equals(uid)){
                        lastMessage.setText("You : " + message.getMessage());
                    }
                    else{
                        lastMessage.setText(message.getMessage());
                    }
                }
                else{
                    lastMessage.setText(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        private ValueEventListener messageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int count = 0;

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Message message = snapshot.getValue(Message.class);

                        if(message.getFrom().equals(chatUid) && !message.isSeen()){
                            count++;
                        }
                    }

                    if(count > 0){
                        messageCount.setVisibility(View.VISIBLE);
                        messageCount.setText(count + "");

                        try {
                            TypedValue typedValue = new TypedValue();
                            Resources.Theme theme = context.getContext().getTheme();
                            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                            @ColorInt int color = typedValue.data;
                            time.setTextColor(color);
                        }catch (Exception e){
                            Log.e("Error", e.getMessage());
                        }

                    }
                    else{
                        try {
                            TypedValue typedValue = new TypedValue();
                            Resources.Theme theme = context.getContext().getTheme();
                            theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true);
                            @ColorInt int color = typedValue.data;
                            time.setTextColor(color);
                        }catch (Exception e){
                            Log.e("Error", e.getMessage());
                        }

                        messageCount.setVisibility(View.GONE);
                    }
                }
                else{
                    try {
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = context.getContext().getTheme();
                        theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true);
                        @ColorInt int color = typedValue.data;
                        time.setTextColor(color);
                    }catch (Exception e){
                        Log.e("Error", e.getMessage());
                    }

                    messageCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        private ValueEventListener chatEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boolean isTyping = (boolean) dataSnapshot.getValue();

                    if(isTyping){
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = context.getContext().getTheme();
                        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                        @ColorInt int color = typedValue.data;
                        lastMessage.setTextColor(color);
                        lastMessage.setText("typing...");
                        lastMessage.setTypeface(lastMessage.getTypeface(), Typeface.ITALIC);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}

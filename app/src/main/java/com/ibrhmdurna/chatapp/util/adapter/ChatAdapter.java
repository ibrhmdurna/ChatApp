package com.ibrhmdurna.chatapp.util.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
        private TextView youText;
        private ImageView photoImage;
        private TextView typingLayout;
        private TextView timeAccentText;

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
            youText = itemView.findViewById(R.id.you_view);
            photoImage = itemView.findViewById(R.id.photo_image);
            typingLayout = itemView.findViewById(R.id.typing_layout);
            timeAccentText = itemView.findViewById(R.id.chat_time_accent_text);
        }

        public void setData(final Chat chat, int position){

            chatUid = chat.getChatUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);

            databaseReference.child("Accounts").child(chatUid).removeEventListener(accountEventListener);
            databaseReference.child("Messages").child(uid).child(chatUid).child(chat.getLast_message_id()).removeEventListener(lastMessageEventListener);
            databaseReference.child("Messages").child(chatUid).child(uid).removeEventListener(messageEventListener);
            databaseReference.child("Chats").child(uid).child(chatUid).child("typing").removeEventListener(chatEventListener);

            databaseReference.child("Accounts").child(chatUid).addListenerForSingleValueEvent(accountEventListener);
            databaseReference.child("Messages").child(uid).child(chatUid).child(chat.getLast_message_id()).addValueEventListener(lastMessageEventListener);
            databaseReference.child("Messages").child(uid).child(chatUid).addValueEventListener(messageEventListener);
            databaseReference.child("Chats").child(uid).child(chatUid).child("typing").addValueEventListener(chatEventListener);

            String chatTime = GetTimeAgo.getInstance().getChatTimeAgo(chat.getTime());
            time.setText(chatTime);
            timeAccentText.setText(chatTime);

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
                picasso.setIndicatorsEnabled(false);
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

                    typingLayout.setVisibility(View.GONE);
                    lastMessage.setVisibility(View.VISIBLE);

                    if(message.getType().equals("Text")){
                        photoImage.setVisibility(View.GONE);
                        lastMessage.setText(message.getMessage());

                        if(message.getFrom().equals(uid)){
                            youText.setVisibility(View.VISIBLE);
                        }
                        else{
                            youText.setVisibility(View.GONE);
                        }
                    }
                    else if (message.getType().equals("Image")){
                        photoImage.setVisibility(View.VISIBLE);

                        if(message.getFrom().equals(uid)){
                            youText.setVisibility(View.VISIBLE);
                        }
                        else{
                            youText.setVisibility(View.GONE);
                        }

                        if(message.getMessage().equals("")){
                            lastMessage.setText("Photo");
                        }
                        else{
                            lastMessage.setText(message.getMessage());
                        }
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
                        timeAccentText.setVisibility(View.VISIBLE);
                        time.setVisibility(View.GONE);

                    }
                    else{
                        timeAccentText.setVisibility(View.GONE);
                        time.setVisibility(View.VISIBLE);
                        messageCount.setVisibility(View.GONE);
                    }
                }
                else{
                    timeAccentText.setVisibility(View.GONE);
                    time.setVisibility(View.VISIBLE);
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
                        typingLayout.setVisibility(View.VISIBLE);
                        lastMessage.setVisibility(View.GONE);
                        photoImage.setVisibility(View.GONE);
                        youText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}

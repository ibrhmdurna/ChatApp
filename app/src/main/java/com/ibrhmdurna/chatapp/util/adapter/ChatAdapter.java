package com.ibrhmdurna.chatapp.util.adapter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.local.ChatActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.GetTimeAgo;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.vanniktech.emoji.EmojiTextView;

import java.util.List;
import java.util.Objects;

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

    class ChatViewHolder extends RecyclerView.ViewHolder{

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
        private String image;

        ChatViewHolder(@NonNull View itemView) {
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

        private void setData(final Chat chat, int position){

            chatUid = chat.getChatUid();

            Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).child(chatUid).child("typing").removeEventListener(chatEventListener);

            Firebase.getInstance().getDatabaseReference().child("Blocks").child(chatUid).child(uid).addValueEventListener(blocksEventListener);
            Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).limitToLast(1).addListenerForSingleValueEvent(lastMessageEventListener);
            Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).addListenerForSingleValueEvent(messageEventListener);

            String chatTime = GetTimeAgo.getInstance().getChatTimeAgo(context.getContext(), chat.getTime());
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
                    final AlertDialog dialog = DialogController.getInstance().dialogChat(context, chatUid);
                    dialog.show();

                    final CheckBox deviceCheck = dialog.findViewById(R.id.delete_device_check);

                    LinearLayout markItem = dialog.findViewById(R.id.mark_as_read_item);
                    assert markItem != null;
                    markItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Update.getInstance().messageSeen(chatUid, true);
                            Update.getInstance().chatSeen(chatUid, true);

                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Update.getInstance().messageSeen(chatUid, false);
                                    Update.getInstance().chatSeen(chatUid, false);
                                }
                            },1500);
                        }
                    });

                    LinearLayout clearItem = dialog.findViewById(R.id.clear_item);
                    assert clearItem != null;
                    clearItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            final AlertDialog confirmDialog = DialogController.getInstance().dialogCustom(context.getActivity(), context.getString(R.string.clear_chat_with) + nameSurname.getText() + context.getString(R.string.clear_chat_with_2), context.getString(R.string.cancel), context.getString(R.string.clear));
                            confirmDialog.show();
                            TextView positiveView = confirmDialog.findViewById(R.id.dialog_positive_btn);
                            assert positiveView != null;
                            positiveView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmDialog.dismiss();
                                    youText.setVisibility(View.GONE);
                                    photoImage.setVisibility(View.GONE);
                                    lastMessage.setVisibility(View.GONE);
                                    timeAccentText.setVisibility(View.GONE);
                                    time.setVisibility(View.VISIBLE);
                                    messageCount.setVisibility(View.GONE);
                                    assert deviceCheck != null;
                                    Delete.getInstance().clearChat(chatUid, deviceCheck.isChecked());
                                }
                            });
                        }
                    });

                    LinearLayout deleteItem = dialog.findViewById(R.id.delete_item);
                    assert deleteItem != null;
                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            final AlertDialog confirmDialog = DialogController.getInstance().dialogCustom(context.getActivity(), context.getString(R.string.delete_chat_with) + nameSurname.getText() + context.getString(R.string.delete_chat_with_2), context.getString(R.string.cancel), context.getString(R.string.delete));
                            confirmDialog.show();
                            TextView positiveView = confirmDialog.findViewById(R.id.dialog_positive_btn);
                            assert positiveView != null;
                            positiveView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmDialog.dismiss();
                                    assert deviceCheck != null;
                                    Delete.getInstance().deleteChat(chatUid, deviceCheck.isChecked());
                                }
                            });
                        }
                    });
                    return true;
                }
            });

        }

        private void setProfileImage(final String value, String nameValue){

            if(image == null){
                image = value;
                if(value.substring(0,8).equals("default_")){
                    String text = value.substring(8,9);
                    int index = Integer.parseInt(text);
                    setProfileImage(index, profileImage);
                    String name = nameValue.substring(0,1);
                    profileText.setText(name);
                    profileText.setVisibility(View.VISIBLE);
                }
                else {
                    if(context != null){
                        try {
                            Glide.with(context).load(value).placeholder(R.drawable.default_avatar).into(profileImage);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    profileText.setText(null);
                    profileText.setVisibility(View.GONE);
                }
            }

            if(!image.equals(value)){
                image = value;
                if(value.substring(0,8).equals("default_")){
                    String text = value.substring(8,9);
                    int index = Integer.parseInt(text);
                    setProfileImage(index, profileImage);
                    String name = nameValue.substring(0,1);
                    profileText.setText(name);
                    profileText.setVisibility(View.VISIBLE);
                }
                else {
                    if(context != null){
                        try {
                            Glide.with(context).load(value).placeholder(R.drawable.default_avatar).into(profileImage);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    profileText.setText(null);
                    profileText.setVisibility(View.GONE);
                }
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

        private ValueEventListener blocksEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        nameSurname.setText(context.getString(R.string.chatapp_user));
                        profileImage.setImageDrawable(Objects.requireNonNull(context.getContext()).getDrawable(R.drawable.default_avatar));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    Firebase.getInstance().getDatabaseReference().child("Accounts").child(chatUid).addListenerForSingleValueEvent(accountEventListener);
                    Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).child(chatUid).child("typing").addValueEventListener(chatEventListener);
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

                    if (account != null){
                        setProfileImage(account.getThumb_image(), account.getName());
                        nameSurname.setText(account.getNameSurname());
                    }
                }
                else{
                    nameSurname.setText(context.getString(R.string.chatapp_user));
                    profileImage.setImageDrawable(Objects.requireNonNull(context.getContext()).getDrawable(R.drawable.default_avatar));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        private ValueEventListener lastMessageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Message message = null;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        message = snapshot.getValue(Message.class);
                    }

                    if(message != null){
                        typingLayout.setVisibility(View.GONE);
                        lastMessage.setVisibility(View.VISIBLE);

                        if(message.isUnsend()){
                            lastMessage.setText(context.getString(R.string.this_message_was_deleted));
                            lastMessage.setTypeface(null, Typeface.ITALIC);
                        }
                        else{
                            lastMessage.setTypeface(Typeface.DEFAULT);

                            switch (message.getType()) {
                                case "Text":
                                    photoImage.setVisibility(View.GONE);
                                    lastMessage.setText(message.getMessage());

                                    if (message.getFrom().equals(uid)) {
                                        youText.setVisibility(View.VISIBLE);
                                    } else {
                                        youText.setVisibility(View.GONE);
                                    }
                                    break;
                                case "Image":

                                    photoImage.setVisibility(View.VISIBLE);

                                    if (message.getMessage().equals("")) {
                                        lastMessage.setText(context.getString(R.string._photo));
                                    } else {
                                        lastMessage.setText(message.getMessage());
                                    }

                                    if (message.getFrom().equals(uid)) {
                                        youText.setVisibility(View.VISIBLE);
                                    } else {
                                        youText.setVisibility(View.GONE);
                                    }
                                    break;
                                case "Voice":

                                    lastMessage.setText("Voice message");

                                    if (message.getFrom().equals(uid)) {
                                        youText.setVisibility(View.VISIBLE);
                                    } else {
                                        youText.setVisibility(View.GONE);
                                    }

                                    break;
                            }
                        }
                    }
                    else {
                        youText.setVisibility(View.GONE);
                        photoImage.setVisibility(View.GONE);
                        lastMessage.setText(null);
                    }

                }
                else{
                    youText.setVisibility(View.GONE);
                    photoImage.setVisibility(View.GONE);
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

                        assert message != null;
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
                    final boolean isTyping = (boolean) dataSnapshot.getValue();

                    Firebase.getInstance().getDatabaseReference().child("Friends").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(chatUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(isTyping){
                                    typingLayout.setVisibility(View.VISIBLE);
                                    lastMessage.setVisibility(View.GONE);
                                    photoImage.setVisibility(View.GONE);
                                    youText.setVisibility(View.GONE);
                                }
                                else{
                                    typingLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
package com.ibrhmdurna.chatapp.Util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.R;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<String> messagesList;

    public MessagesAdapter(List<String> list){
        messagesList = list;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messages_layout, viewGroup, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder messagesViewHolder, int i) {
        messagesViewHolder.setMessages(messagesList.get(i));
        messagesViewHolder.setProfileText(messagesList.get(i).substring(0,1));
        messagesViewHolder.randomProfileImage();
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView nameSurname, messages, profileText;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            nameSurname = itemView.findViewById(R.id.name_surname_text);
            messages = itemView.findViewById(R.id.content_message);
            profileText = itemView.findViewById(R.id.profile_image_text);
            profileImage = itemView.findViewById(R.id.profile_image);
        }

        public void randomProfileImage(){
            Random r = new Random();
            int index = r.nextInt(10);
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

        public void setMessages(String text){
            messages.setText(text);
        }

        public void setProfileText(String text) {
            profileText.setText(text);
        }
    }
}

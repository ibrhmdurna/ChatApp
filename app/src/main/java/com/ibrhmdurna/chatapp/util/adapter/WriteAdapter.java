package com.ibrhmdurna.chatapp.util.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WriteAdapter extends RecyclerView.Adapter<WriteAdapter.WriteViewHolder> {

    private Context context;
    private List<Friend> friendList;

    private OnItemClickListener listener;

    public WriteAdapter(Context context, List<Friend> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    public interface OnItemClickListener{
        void onItemClick(String uid);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public WriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout, viewGroup, false);
        return new WriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WriteViewHolder writeViewHolder, int i) {
        Friend friend = friendList.get(i);
        writeViewHolder.setData(friend, i);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class WriteViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView profileText;
        private TextView nameSurname, email;
        private View line;

        public WriteViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.friend_profile_image);
            profileText = itemView.findViewById(R.id.friend_profile_image_text);
            nameSurname = itemView.findViewById(R.id.friend_name_surname);
            email = itemView.findViewById(R.id.friend_email);
            line = itemView.findViewById(R.id.friend_line);
        }

        private void setData(final Friend friend, final int position){

            Firebase.getInstance().getDatabaseReference().child("Accounts").child(friend.getAccount().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Account account = dataSnapshot.getValue(Account.class);
                        account.setUid(dataSnapshot.getKey());
                        friend.setAccount(account);

                        nameSurname.setText(friend.getAccount().getNameSurname());
                        email.setText(friend.getAccount().getEmail());
                        setProfileImage(friend.getAccount().getThumb_image(), friend.getAccount().getName(), profileImage, profileText);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(position == 0){
                line.setVisibility(View.GONE);
            }
            else{
                line.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(friend.getAccount().getUid());
                        }
                    }
                }
            });
        }
    }

    private void setProfileImage(final String value, String nameValue, final CircleImageView profileImage, TextView profileText){

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
                Glide.with(context).load(value).placeholder(R.drawable.default_avatar).into(profileImage);
            }
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

    public void filterList(List<Friend> filterList){
        friendList = filterList;
        notifyDataSetChanged();
    }
}

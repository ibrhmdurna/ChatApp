package com.ibrhmdurna.chatapp.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friendList;

    public FriendAdapter(Context context, List<Friend> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout, viewGroup, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder friendViewHolder, int i) {

        final Friend friend = friendList.get(i);

        friendViewHolder.setData(friend, i);

    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView profileText;
        private TextView nameSurname, email;
        private ImageView onlineView;

        private View line;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.friend_profile_image);
            profileText = itemView.findViewById(R.id.friend_profile_image_text);
            nameSurname = itemView.findViewById(R.id.friend_name_surname);
            email = itemView.findViewById(R.id.friend_email);
            line = itemView.findViewById(R.id.friend_line);
            onlineView = itemView.findViewById(R.id.friend_online_icon);
        }

        private void setData(final Friend friend, int position){

            nameSurname.setText(friend.getAccount().getNameSurname());
            email.setText(friend.getAccount().getEmail());
            setProfileImage(friend.getAccount().getThumb_image(), friend.getAccount().getName());

            if(position == friendList.size() - 1){
                line.setVisibility(View.GONE);
            }
            else{
                line.setVisibility(View.VISIBLE);
            }

            if(friend.getAccount().isOnline()){
                onlineView.setVisibility(View.VISIBLE);
            }
            else{
                onlineView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("user_id", friend.getAccount().getUid());
                    context.startActivity(profileIntent);
                }
            });
        }

        public void setProfileImage(final String value, String nameValue){

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
    }
    public void filterList(List<Friend> filterList){
        friendList = filterList;
        notifyDataSetChanged();
    }
}

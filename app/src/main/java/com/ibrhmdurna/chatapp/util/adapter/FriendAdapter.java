package com.ibrhmdurna.chatapp.util.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Friend> friendList;
    private int layout;

    private OnItemClickListener listener;

    private String uid;

    public FriendAdapter(Context context, List<Friend> friendList, int layout) {
        this.context = context;
        this.friendList = friendList;
        this.layout = layout;
        uid = FirebaseAuth.getInstance().getUid();
    }

    public interface OnItemClickListener{
        void onItemClick(String uid);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i){
            case 0:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout, viewGroup, false);
                return new FriendViewHolder(view);
            case 1:
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_friend_layout, viewGroup, false);
                return new ProfileFriendViewHolder(view1);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Friend friend = friendList.get(i);

        switch (viewHolder.getItemViewType()){
            case 0:
                FriendViewHolder friendViewHolder = (FriendViewHolder)viewHolder;
                friendViewHolder.setData(friend, i);
                break;
            case 1:
                ProfileFriendViewHolder profileFriendViewHolder = (ProfileFriendViewHolder)viewHolder;
                profileFriendViewHolder.setData(friend, i);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return layout;
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
            setProfileImage(friend.getAccount().getThumb_image(), friend.getAccount().getName(), profileImage, profileText);

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

    public class ProfileFriendViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView profileText;
        private TextView nameSurname, email;
        private TextView addItem, cancelItem, deleteItem;

        private View line;

        private String friendUid;

        public ProfileFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.friend_profile_image);
            profileText = itemView.findViewById(R.id.friend_profile_image_text);
            nameSurname = itemView.findViewById(R.id.friend_name_surname);
            email = itemView.findViewById(R.id.friend_email);
            line = itemView.findViewById(R.id.friend_line);
            addItem = itemView.findViewById(R.id.friend_add_item);
            cancelItem = itemView.findViewById(R.id.friend_cancel_item);
            deleteItem = itemView.findViewById(R.id.friend_delete_item);
        }

        private void setData(final Friend friend, int position){

            nameSurname.setText(friend.getAccount().getNameSurname());
            email.setText(friend.getAccount().getEmail());
            setProfileImage(friend.getAccount().getThumb_image(), friend.getAccount().getName(), profileImage, profileText);

            if(position == friendList.size() - 1){
                line.setVisibility(View.GONE);
            }
            else{
                line.setVisibility(View.VISIBLE);
            }

            friendUid = friend.getAccount().getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);

            databaseReference.child("Request").child(friend.getAccount().getUid()).removeEventListener(requestEventListener);
            databaseReference.child("Friends").child(uid).child(friendUid).removeEventListener(friendEventListener);

            if(!friendUid.equals(uid)){
                databaseReference.child("Request").child(friend.getAccount().getUid()).addListenerForSingleValueEvent(requestEventListener);
            }
            else{
                addItem.setVisibility(View.GONE);
                cancelItem.setVisibility(View.GONE);
                deleteItem.setVisibility(View.GONE);
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

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Insert.getInstance().request(friend.getAccount().getUid());
                    addItem.setVisibility(View.GONE);
                    cancelItem.setVisibility(View.VISIBLE);
                    deleteItem.setVisibility(View.GONE);
                }
            });

            cancelItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Delete.getInstance().myRequest(friend.getAccount().getUid());
                    addItem.setVisibility(View.VISIBLE);
                    cancelItem.setVisibility(View.GONE);
                    deleteItem.setVisibility(View.GONE);
                }
            });

            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = DialogController.getInstance().dialogCustom((Activity) context, null, "Cancel", "Delete");

                    String text = "Are you sure you want to make " + friend.getAccount().getNameSurname() + " out of friendship?";
                    TextView content = dialog.findViewById(R.id.dialog_content_text);

                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = context.getTheme();
                    theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                    @ColorInt int color = typedValue.data;

                    SpannableString ss = new SpannableString(text);
                    ForegroundColorSpan fcsColor = new ForegroundColorSpan(color);

                    ss.setSpan(fcsColor, 30, 30 + friend.getAccount().getNameSurname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setText(ss);

                    TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Delete.getInstance().friend(friend.getAccount().getUid());
                            addItem.setVisibility(View.VISIBLE);
                            cancelItem.setVisibility(View.GONE);
                            deleteItem.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }

        private ValueEventListener requestEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    addItem.setVisibility(View.GONE);
                    deleteItem.setVisibility(View.GONE);
                    cancelItem.setVisibility(View.VISIBLE);
                }
                else{
                    cancelItem.setVisibility(View.GONE);
                    addItem.setVisibility(View.GONE);
                    deleteItem.setVisibility(View.GONE);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.keepSynced(true);

                    databaseReference.child("Friends").child(uid).child(friendUid).addListenerForSingleValueEvent(friendEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        private ValueEventListener friendEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    deleteItem.setVisibility(View.VISIBLE);
                    addItem.setVisibility(View.GONE);
                    cancelItem.setVisibility(View.GONE);
                }
                else{
                    addItem.setVisibility(View.VISIBLE);
                    cancelItem.setVisibility(View.GONE);
                    deleteItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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

    public void filterList(List<Friend> filterList){
        friendList = filterList;
        notifyDataSetChanged();
    }
}

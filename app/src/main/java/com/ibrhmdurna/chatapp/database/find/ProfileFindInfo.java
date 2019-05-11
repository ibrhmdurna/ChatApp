package com.ibrhmdurna.chatapp.database.find;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.databinding.ActivityProfileBinding;
import com.ibrhmdurna.chatapp.models.Account;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFindInfo implements IFind {

    private ActivityProfileBinding binding;
    private CircleImageView profileImage;
    private TextView profileText;
    private LinearLayout phoneLayout;

    private LinearLayout confirmLayout;
    private RelativeLayout addFriendLayout, sendMessageView;
    private TextView cancelRequest, addFriendView, friendInfoText;
    private TextView friendsItem, mutualItem;
    private ImageView onlineView;

    private RelativeLayout rootView;
    private SpinKitView loadingBar;

    private final String uid;
    private String current_uid;

    public ProfileFindInfo(ActivityProfileBinding binding,String uid) {
        this.binding = binding;
        this.uid = uid;
    }

    @Override
    public void buildView() {
        profileImage = binding.getRoot().findViewById(R.id.profileImage);
        profileText = binding.getRoot().findViewById(R.id.profileImageText);
        phoneLayout = binding.getRoot().findViewById(R.id.profilePhoneLayout);
        cancelRequest = binding.getRoot().findViewById(R.id.cancel_req_btn);
        addFriendView = binding.getRoot().findViewById(R.id.add_friend_btn);
        confirmLayout = binding.getRoot().findViewById(R.id.profile_confirm_layout);
        addFriendLayout = binding.getRoot().findViewById(R.id.add_friend_layout);
        sendMessageView = binding.getRoot().findViewById(R.id.sendMessageView);
        friendInfoText = binding.getRoot().findViewById(R.id.friendInfoText);
        rootView = binding.getRoot().findViewById(R.id.rootView);
        loadingBar = binding.getRoot().findViewById(R.id.loadingBar);
        friendsItem = binding.getRoot().findViewById(R.id.profileFriendsCount);
        mutualItem = binding.getRoot().findViewById(R.id.profileMutualCount);
        onlineView = binding.getRoot().findViewById(R.id.profileOnlineView);
    }

    @Override
    public void getContent() {
        current_uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).removeEventListener(accountEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(current_uid).child(uid).removeEventListener(friendEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(friendEventListener2);

        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).addValueEventListener(accountEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(current_uid).child(uid).addValueEventListener(friendEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).addValueEventListener(friendEventListener2);
    }

    @Override
    public void getMore() {
        // NOTHING...
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).removeEventListener(accountEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(current_uid).child(uid).removeEventListener(friendEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(friendEventListener2);
    }

    private ValueEventListener accountEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                final Account account = dataSnapshot.getValue(Account.class);

                String image = account.getProfile_image();

                if(image.substring(0, 8).equals("default_")){
                    String value = image.substring(8,9);
                    int index = Integer.parseInt(value);
                    setProfileImage(index, profileImage);
                    String name = account.getName().substring(0,1);
                    profileText.setText(name);
                    profileText.setVisibility(View.VISIBLE);
                }
                else {
                    if(binding.getRoot().getContext() != null){
                        Glide.with(binding.getRoot().getContext()).load(account.getProfile_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                    }
                    profileText.setText(null);
                    profileText.setVisibility(View.GONE);
                }

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(friendInfoText.getVisibility() == View.VISIBLE){
                            if(account.isOnline()){
                                onlineView.setVisibility(View.VISIBLE);
                            }
                            else{
                                Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dataSnapshot.child("online").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    boolean isOnline = (boolean) dataSnapshot.getValue();

                                                    if(isOnline){
                                                        onlineView.setVisibility(View.VISIBLE);
                                                    }
                                                    else{
                                                        onlineView.setVisibility(View.GONE);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                },1500);
                            }
                        }
                    }
                }, 1000);

                convertGender(account);
                convertLocation(account);

                phoneLayout.setVisibility(account.getPhone().trim().length() > 0 ? View.VISIBLE : View.GONE);

                binding.setAccount(account);
                rootView.setVisibility(View.VISIBLE);
                loadingBar.setIndeterminate(false);
                loadingBar.setVisibility(View.GONE);
            }
            else {
                Toast.makeText(binding.getRoot().getContext(), binding.getRoot().getContext().getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
                rootView.setVisibility(View.VISIBLE);
                loadingBar.setIndeterminate(false);
                loadingBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(binding.getRoot().getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            rootView.setVisibility(View.VISIBLE);
            loadingBar.setIndeterminate(false);
            loadingBar.setVisibility(View.GONE);
        }
    };

    private ValueEventListener friendEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                addFriendLayout.setVisibility(View.GONE);
                sendMessageView.setVisibility(View.VISIBLE);
                friendInfoText.setVisibility(View.VISIBLE);
                confirmLayout.setVisibility(View.GONE);
            }
            else {
                Firebase.getInstance().getDatabaseReference().child("Request").child(uid).child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            cancelRequest.setVisibility(View.VISIBLE);
                            addFriendView.setVisibility(View.GONE);
                        }
                        else {
                            cancelRequest.setVisibility(View.GONE);
                            addFriendView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Firebase.getInstance().getDatabaseReference().child("Request").child(current_uid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            confirmLayout.setVisibility(View.VISIBLE);
                        }
                        else {
                            confirmLayout.setVisibility(View.GONE);
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

    private ValueEventListener friendEventListener2 = new ValueEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                friendsItem.setText(dataSnapshot.getChildrenCount() + " " +binding.getRoot().getContext().getString(R.string._friends));

                final int[] count = {0};
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(!current_uid.equals(snapshot.getKey())){
                        Firebase.getInstance().getDatabaseReference().child("Friends").child(current_uid).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    count[0]++;
                                    mutualItem.setText(count[0] + " " + binding.getRoot().getContext().getString(R.string.mutual_f));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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

    private void convertGender(Account account){
        String[] genders = binding.getRoot().getContext().getResources().getStringArray(R.array.gender);
        account.setConvertGender(genders[account.getGender() - 1]);
    }

    private void convertLocation(Account account){
        String[] locations = binding.getRoot().getContext().getResources().getStringArray(R.array.countries_array);
        account.setConvertLocation(locations[account.getLocation() - 1]);
    }
}

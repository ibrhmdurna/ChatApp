package com.ibrhmdurna.chatapp.database.find;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.databinding.ActivityProfileBinding;
import com.ibrhmdurna.chatapp.models.Account;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFindInfo implements IFind {

    private ActivityProfileBinding binding;
    private CircleImageView profileImage;
    private TextView profileText;
    private LinearLayout phoneLayout;

    private LinearLayout confirmLayout;
    private RelativeLayout addFriendLayout, sendMessageView;
    private TextView cancelRequest, addFriendView, friendInfoText;

    private RelativeLayout rootView;
    private SpinKitView loadingBar;

    private String uid;

    public ProfileFindInfo(ActivityProfileBinding binding,String uid) {
        this.binding = binding;
        this.uid = uid;
        buildView();
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
    }

    @Override
    public void getContent() {
        final String current_uid = FirebaseAuth.getInstance().getUid();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        database.child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        final Picasso picasso = Picasso.get();
                        picasso.setIndicatorsEnabled(true);
                        picasso.load(account.getProfile_image()).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_avatar).into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                picasso.load(account.getProfile_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                            }
                        });
                        profileText.setText(null);
                        profileText.setVisibility(View.GONE);
                    }

                    convertGender(account);
                    convertLocation(account);

                    phoneLayout.setVisibility(account.getPhone().trim().length() > 0 ? View.VISIBLE : View.GONE);

                    binding.setAccount(account);
                    rootView.setVisibility(View.VISIBLE);
                    loadingBar.setIndeterminate(false);
                    loadingBar.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(binding.getRoot().getContext(), "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
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
        });

        database.child("Friends").child(current_uid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    addFriendLayout.setVisibility(View.GONE);
                    sendMessageView.setVisibility(View.VISIBLE);
                    friendInfoText.setVisibility(View.VISIBLE);
                    confirmLayout.setVisibility(View.GONE);
                }
                else {
                    database.child("Request").child(uid).child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    database.child("Request").child(current_uid).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
        });
    }

    @Override
    public void getMore() {
        // NOTHING...
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

    private void convertGender(Account account){
        String[] genders = binding.getRoot().getContext().getResources().getStringArray(R.array.gender);
        account.setConvertGender(genders[account.getGender() - 1]);
    }

    private void convertLocation(Account account){
        String[] locations = binding.getRoot().getContext().getResources().getStringArray(R.array.countries_array);
        account.setConvertLocation(locations[account.getLocation() - 1]);
    }
}

package com.ibrhmdurna.chatapp.database.find;

import android.annotation.SuppressLint;
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
import com.ibrhmdurna.chatapp.databinding.FragmentAccountBinding;
import com.ibrhmdurna.chatapp.models.Account;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFindInfo implements IFind {

    private FragmentAccountBinding binding;
    private RelativeLayout rootView;
    private SpinKitView loadingBar;
    private CircleImageView profileImage;
    private TextView profileText;
    private LinearLayout phoneLayout;
    private TextView friendCount;

    private String uid;

    public AccountFindInfo(FragmentAccountBinding binding) {
        this.binding = binding;
        buildView();
    }

    @Override
    public void buildView() {
        rootView = binding.getRoot().findViewById(R.id.rootView);
        loadingBar = binding.getRoot().findViewById(R.id.loadingBar);
        profileImage = binding.getRoot().findViewById(R.id.profileImage);
        profileText = binding.getRoot().findViewById(R.id.profileImageText);
        phoneLayout = binding.getRoot().findViewById(R.id.account_phone_layout);
        friendCount = binding.getRoot().findViewById(R.id.accountFriendText);
    }

    @Override
    public void getContent() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Accounts");
        database.keepSynced(true);

        database.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    getMore();
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
    }

    @Override
    public void getMore() {
        FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    friendCount.setText(dataSnapshot.getChildrenCount() + " Friends");
                    rootView.setVisibility(View.VISIBLE);
                    loadingBar.setIndeterminate(false);
                    loadingBar.setVisibility(View.GONE);
                }
                else{
                    rootView.setVisibility(View.VISIBLE);
                    loadingBar.setIndeterminate(false);
                    loadingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

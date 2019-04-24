package com.ibrhmdurna.chatapp.database.find;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareFindInfo implements IFind {

    private Activity context;

    private CircleImageView profileImage;
    private TextView profileText;

    private String uid;

    public ShareFindInfo(Activity context, String uid) {
        this.context = context;
        this.uid = uid;
        buildView();
    }

    @Override
    public void buildView() {
        profileImage = context.findViewById(R.id.profile_image);
        profileText = context.findViewById(R.id.profile_text);
    }

    @Override
    public void getContent() {

        FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        picasso.load(account.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_avatar).into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                picasso.load(account.getThumb_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                            }
                        });
                        profileText.setText(null);
                        profileText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void getMore() {

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

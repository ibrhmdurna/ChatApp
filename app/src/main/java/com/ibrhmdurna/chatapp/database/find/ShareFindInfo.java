package com.ibrhmdurna.chatapp.database.find;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareFindInfo implements IFind {

    private Activity context;

    private CircleImageView profileImage;
    private TextView profileText;

    private String uid;

    public ShareFindInfo(Activity context, String uid) {
        this.context = context;
        this.uid = uid;
    }

    @Override
    public void buildView() {
        profileImage = context.findViewById(R.id.profile_image);
        profileText = context.findViewById(R.id.profile_text);
    }

    @Override
    public void getContent() {
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).addListenerForSingleValueEvent(contentEventListener);
    }

    @Override
    public void getMore() {

    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Accounts").child(uid).removeEventListener(contentEventListener);
    }

    private ValueEventListener contentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                final Account account = dataSnapshot.getValue(Account.class);

                if(account != null){
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
                        if(context != null){
                            try {
                                Glide.with(context).load(account.getThumb_image()).placeholder(R.drawable.default_avatar).into(profileImage);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        profileText.setText(null);
                        profileText.setVisibility(View.GONE);
                    }
                }
                else{
                    Toast.makeText(context, context.getString(R.string.couldnt_refresh_feed), Toast.LENGTH_SHORT).show();
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
}

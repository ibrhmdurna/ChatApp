package com.ibrhmdurna.chatapp.database.find;

import android.content.Context;
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
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFindInfo implements IFind {

    private Context context;

    private CircleImageView profileImage;
    private TextView profileText;
    private TextView nameSurname;
    private TextView lastSeen;

    private String uid;

    public ChatFindInfo(Context context, CircleImageView profileImage, TextView profileText, TextView nameSurname, TextView lastSeen, String uid) {
        this.context = context;
        this.profileImage = profileImage;
        this.profileText = profileText;
        this.nameSurname = nameSurname;
        this.lastSeen = lastSeen;
        this.uid = uid;
    }

    @Override
    public void getInformation() {

        FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Account account = dataSnapshot.getValue(Account.class);

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
                        UniversalImageLoader.setImage(account.getThumb_image(), profileImage, null, "");
                        profileText.setText(null);
                        profileText.setVisibility(View.GONE);
                    }

                    nameSurname.setText(account.getNameSurname());
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
}

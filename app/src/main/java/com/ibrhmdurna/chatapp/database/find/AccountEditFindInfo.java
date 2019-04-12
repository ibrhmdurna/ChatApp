package com.ibrhmdurna.chatapp.database.find;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.databinding.ActivityEditAccountBinding;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountEditFindInfo implements IFind {

    private ActivityEditAccountBinding binding;
    private CircleImageView profileImage;
    private TextView profileText;

    public AccountEditFindInfo(ActivityEditAccountBinding binding) {
        this.binding = binding;
        buildView();
    }

    @Override
    public void buildView() {
        profileImage = binding.getRoot().findViewById(R.id.profileImage);
        profileText = binding.getRoot().findViewById(R.id.profileImageText);
    }

    @Override
    public void getInformation() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //DatabaseReference database = FirebaseDB.getInstance().getDatabase().child("Accounts");

        FirebaseDatabase.getInstance().getReference().child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }
                    else {
                        UniversalImageLoader.setImage(account.getThumb_image(), profileImage, null, "");
                        profileText.setVisibility(View.GONE);
                    }

                    binding.setAccount(account);
                }
                else {
                    Toast.makeText(binding.getRoot().getContext(), "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(binding.getRoot().getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

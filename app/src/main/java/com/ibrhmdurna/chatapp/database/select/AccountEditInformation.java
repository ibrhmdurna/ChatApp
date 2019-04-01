package com.ibrhmdurna.chatapp.database.select;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.FirebaseDB;
import com.ibrhmdurna.chatapp.database.bridgeSelect.Implementor;
import com.ibrhmdurna.chatapp.databinding.ActivityEditAccountBinding;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountEditInformation implements Implementor {

    private ActivityEditAccountBinding binding;
    private CircleImageView profileImage;
    private TextView profileText;
    private SmartMaterialSpinner locationSpinner;

    public AccountEditInformation(ActivityEditAccountBinding binding, CircleImageView profileImage, TextView profileText, SmartMaterialSpinner locationSpinner) {
        this.binding = binding;
        this.profileImage = profileImage;
        this.profileText = profileText;
        this.locationSpinner = locationSpinner;
    }

    @Override
    public void getAccountInformation() {
        String uid = FirebaseDB.getInstance().getCurrentUser().getUid();

        FirebaseDB.getInstance().getDatabase().child("Accounts").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        UniversalImageLoader.setImage(account.getProfile_image(), profileImage, null, null);
                        profileText.setVisibility(View.GONE);
                    }

                    //setSelectLocation(account.getLocation());

                    binding.setAccount(account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void setProfileImage(int index, CircleImageView profileImage) {
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

    private void setSelectLocation(String value){
        for(int i = 0; i < locationSpinner.getCount(); i++){
            if(locationSpinner.getItemAtPosition(i).toString().equals(value)){
                locationSpinner.setSelection(i + 1);
                break;
            }
        }
    }
}

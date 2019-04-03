package com.ibrhmdurna.chatapp.database.select;

import android.content.Context;
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
import com.ibrhmdurna.chatapp.database.bridgeSelect.Implementor;
import com.ibrhmdurna.chatapp.databinding.FragmentAccountBinding;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInfo implements Implementor {

    private Context context;
    private FragmentAccountBinding binding;
    private RelativeLayout rootView;
    private SpinKitView loadingBar;
    private CircleImageView profileImage;
    private TextView profileText;
    private LinearLayout phoneLayout;

    public AccountInfo(Context context, FragmentAccountBinding binding, RelativeLayout rootView, SpinKitView loadingBar, CircleImageView profileImage, TextView profileText, LinearLayout phoneLayout) {
        this.context = context;
        this.binding = binding;
        this.rootView = rootView;
        this.loadingBar = loadingBar;
        this.profileImage = profileImage;
        this.profileText = profileText;
        this.phoneLayout = phoneLayout;
    }

    @Override
    public void getAccountInformation() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Accounts");
        database.keepSynced(true);

        database.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        UniversalImageLoader.setImage(account.getProfile_image(), profileImage, null, "");
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
                    Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
                    rootView.setVisibility(View.VISIBLE);
                    loadingBar.setIndeterminate(false);
                    loadingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                rootView.setVisibility(View.VISIBLE);
                loadingBar.setIndeterminate(false);
                loadingBar.setVisibility(View.GONE);
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

    private void convertGender(Account account){
        String[] genders = context.getResources().getStringArray(R.array.gender);
        account.setConvertGender(genders[account.getGender() - 1]);
    }

    private void convertLocation(Account account){
        String[] locations = context.getResources().getStringArray(R.array.countries_array);
        account.setConvertLocation(locations[account.getLocation() - 1]);
    }
}

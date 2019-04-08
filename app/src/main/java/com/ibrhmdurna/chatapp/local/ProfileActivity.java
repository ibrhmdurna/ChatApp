package com.ibrhmdurna.chatapp.local;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.ProfileFindInfo;
import com.ibrhmdurna.chatapp.databinding.ActivityProfileBinding;
import com.ibrhmdurna.chatapp.util.dialog.MoreBottomSheetDialog;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, MoreBottomSheetDialog.BottomSheetListener {

    private ActivityProfileBinding binding;
    private CircleImageView profileImage;
    private TextView profileText;
    private LinearLayout phoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        toolsManagement();
    }

    private void getProfileInformation(){
        String uid = getIntent().getStringExtra("user_id");

        AbstractFind find = new Find(new ProfileFindInfo(this, binding, profileImage, profileText, phoneLayout, uid));
        find.getInformation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProfileInformation();
    }

    @Override
    public void toolsManagement() {
        buildView();
    }

    @Override
    public void buildView() {
        profileImage = findViewById(R.id.profileImage);
        profileText = findViewById(R.id.profileImageText);
        phoneLayout = findViewById(R.id.profilePhoneLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_back_view:
                super.onBackPressed();
                break;
            case R.id.profile_options_view:
                MoreBottomSheetDialog moreBottomSheetDialog = new MoreBottomSheetDialog(true);
                moreBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
            case R.id.profileFriendsView:
                Intent friends = new Intent(this, FriendsActivity.class);
                friends.putExtra("isAccount", false);
                startActivity(friends);
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "share":
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello, I Name Surname (@email) would you like to join us? ChatApp Inc.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                break;
        }
    }
}

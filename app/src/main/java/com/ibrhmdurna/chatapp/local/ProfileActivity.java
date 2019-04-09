package com.ibrhmdurna.chatapp.local;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.ProfileFindInfo;
import com.ibrhmdurna.chatapp.databinding.ActivityProfileBinding;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.ibrhmdurna.chatapp.util.dialog.MoreBottomSheetDialog;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, MoreBottomSheetDialog.BottomSheetListener {

    private ActivityProfileBinding binding;
    private CircleImageView profileImage;
    private TextView profileText;
    private LinearLayout phoneLayout;

    private LinearLayout confirmLayout;
    private RelativeLayout addFriendLayout, sendMessageView;
    private TextView cancelReqView, addFriendView, friendInfoText;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        toolsManagement();
    }

    private void getProfileInformation(){
        uid = getIntent().getStringExtra("user_id");

        AbstractFind find = new Find(new ProfileFindInfo(this, binding, profileImage, profileText, phoneLayout, cancelReqView, addFriendLayout, addFriendView, confirmLayout, sendMessageView, friendInfoText, uid));
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
        cancelReqView = findViewById(R.id.cancel_req_btn);
        addFriendView = findViewById(R.id.add_friend_btn);
        confirmLayout = findViewById(R.id.profile_confirm_layout);
        addFriendLayout = findViewById(R.id.add_friend_layout);
        sendMessageView = findViewById(R.id.sendMessageView);
        friendInfoText = findViewById(R.id.friendInfoText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_back_view:
                super.onBackPressed();
                break;
            case R.id.profile_options_view:
                MoreBottomSheetDialog moreBottomSheetDialog = new MoreBottomSheetDialog(uid);
                moreBottomSheetDialog.show(getSupportFragmentManager(), "bottom_sheet");
                break;
            case R.id.profileFriendsView:
                Intent friends = new Intent(this, FriendsActivity.class);
                friends.putExtra("isAccount", false);
                startActivity(friends);
                break;
            case R.id.add_friend_btn:
                Insert.getInstance().request(uid);
                addFriendView.setVisibility(View.GONE);
                cancelReqView.setVisibility(View.VISIBLE);
                break;
            case R.id.cancel_req_btn:
                Delete.getInstance().myRequest(uid);
                addFriendView.setVisibility(View.VISIBLE);
                cancelReqView.setVisibility(View.GONE);
                break;
            case R.id.confirm_cancel_btn:
                Delete.getInstance().request(uid);
                confirmLayout.setVisibility(View.GONE);
                break;
            case R.id.confirm_btn:
                Insert.getInstance().friend(uid);
                confirmLayout.setVisibility(View.GONE);
                addFriendLayout.setVisibility(View.GONE);
                friendInfoText.setVisibility(View.VISIBLE);
                sendMessageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "share":
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello, I "+ binding.getAccount().getNameSurname() +" (" + binding.getAccount().getEmail() + ") would you like to join us? ChatApp Inc.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                break;
            case "block":
                break;
            case "report":
                break;
            case "delete":
                String content = String.valueOf(Html.fromHtml("Are you sure you want to make <b> " + binding.getAccount().getNameSurname() + " </b> out of friendship?"));
                final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, content, "Cancel", "Delete");
                TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Delete.getInstance().friend(uid);
                        addFriendLayout.setVisibility(View.VISIBLE);
                        addFriendView.setVisibility(View.VISIBLE);
                        cancelReqView.setVisibility(View.GONE);
                        friendInfoText.setVisibility(View.GONE);
                        sendMessageView.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }
}

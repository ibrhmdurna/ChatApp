package com.ibrhmdurna.chatapp.local;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Connection;
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

    private LinearLayout confirmLayout;
    private RelativeLayout addFriendLayout, sendMessageView;
    private TextView cancelReqView, addFriendView, friendInfoText;

    private AbstractFind find;

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

        find = new Find(new ProfileFindInfo(binding, uid));
        find.getContent();
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
                Intent friends = new Intent(this, ProfileFriendsActivity.class);
                friends.putExtra("user_id", uid);
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
            case R.id.sendMessageBtn:
                Intent chatIntent = new Intent(this, ChatActivity.class);
                chatIntent.putExtra("user_id", uid);
                startActivity(chatIntent);
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        switch (action){
            case "share":
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello, I am "+ binding.getAccount().getNameSurname() +" (" + binding.getAccount().getEmail() + ") would you like to join us? ChatApp Inc.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                break;
            case "block":
                break;
            case "report":
                break;
            case "delete":
                final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, null, "Cancel", "Delete");

                String text = "Are you sure you want to make " + binding.getAccount().getNameSurname() + " out of friendship?";
                TextView content = dialog.findViewById(R.id.dialog_content_text);

                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getTheme();
                theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                @ColorInt int color = typedValue.data;

                SpannableString ss = new SpannableString(text);
                ForegroundColorSpan fcsColor = new ForegroundColorSpan(color);

                ss.setSpan(fcsColor, 30, 30 + binding.getAccount().getNameSurname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setText(ss);

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

    @Override
    protected void onResume() {
        super.onResume();
        Connection.getInstance().onConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Connection.getInstance().onDisconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        find.onDestroy();
    }
}

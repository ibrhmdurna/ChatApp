package com.ibrhmdurna.chatapp.local;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.ProfileFindInfo;
import com.ibrhmdurna.chatapp.databinding.ActivityProfileBinding;
import com.ibrhmdurna.chatapp.image.ImageActivity;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.ibrhmdurna.chatapp.util.dialog.MoreBottomSheetDialog;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener, MoreBottomSheetDialog.BottomSheetListener {

    private ActivityProfileBinding binding;

    private LinearLayout confirmLayout;
    private RelativeLayout addFriendLayout, sendMessageView;
    private TextView cancelReqView, addFriendView, friendInfoText;

    private AbstractFind find;

    private String uid;

    private boolean isBlock = false;

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

    private void clearNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(uid, 0);
        manager.cancel(uid, 1);
    }

    private void onBack(){
        if(isBlock){
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra("page", "Main");
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
        }
        else{
            super.onBackPressed();
        }
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
                onBack();
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
            case R.id.unblock_btn:
                final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, getString(R.string.are_you_want_to_unblock), getString(R.string.cancel), getString(R.string.unblock));
                TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                assert positiveBtn != null;
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Delete.getInstance().unblock(uid);
                        if(find != null){
                            find.onDestroy();
                            find.getContent();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onButtonClicked(String action) {
        String language = Locale.getDefault().getLanguage();

        switch (action){
            case "share":
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.hello_i_am) + " " + binding.getAccount().getNameSurname() +" (" + binding.getAccount().getEmail() + ") "+ getString(R.string.would_join_chatapp);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.subject_here);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
                break;
            case "block":
                final AlertDialog dialogBlock = DialogController.getInstance().dialogCustom(this, null, getString(R.string.cancel), getString(R.string.block));
                String txt;

                if(language.equals("tr")){
                    txt = binding.getAccount().getNameSurname() + " adlı kullanıcıyı engellemek istediğinden emin misin?";
                }
                else{
                    txt = "Are you sure yo want to block " + binding.getAccount().getNameSurname() + "?";
                }

                TextView contentBlock = dialogBlock.findViewById(R.id.dialog_content_text);

                TypedValue typedValueB = new TypedValue();
                Resources.Theme themeB = getTheme();
                themeB.resolveAttribute(R.attr.colorAccent, typedValueB, true);
                @ColorInt int colorB = typedValueB.data;

                SpannableString ssB = new SpannableString(txt);
                ForegroundColorSpan fcsColorB = new ForegroundColorSpan(colorB);

                if(language.equals("tr")){
                    ssB.setSpan(fcsColorB, 0, binding.getAccount().getNameSurname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    assert contentBlock != null;
                    contentBlock.setText(ssB);
                }
                else{
                    ssB.setSpan(fcsColorB, 30, 30 + binding.getAccount().getNameSurname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    assert contentBlock != null;
                    contentBlock.setText(ssB);
                }

                TextView positiveBtnB = dialogBlock.findViewById(R.id.dialog_positive_btn);
                assert positiveBtnB != null;
                positiveBtnB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBlock.dismiss();
                        Insert.getInstance().block(uid);
                        isBlock = true;
                    }
                });
                break;
            case "report":
                break;
            case "delete":
                final AlertDialog dialog = DialogController.getInstance().dialogCustom(this, null, getString(R.string.cancel), getString(R.string.delete));

                String text;
                if(language.equals("tr")){
                    text = binding.getAccount().getNameSurname() + " arkadaşlıktan çıkarmak istediğine emin misin?";
                }
                else{
                    text = "Are you sure you want to make " +  binding.getAccount().getNameSurname() + " out of friendship?";
                }

                TextView content = dialog.findViewById(R.id.dialog_content_text);

                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getTheme();
                theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                @ColorInt int color = typedValue.data;

                SpannableString ss = new SpannableString(text);
                ForegroundColorSpan fcsColor = new ForegroundColorSpan(color);

                if(language.equals("tr")){
                    ss.setSpan(fcsColor, 0, binding.getAccount().getNameSurname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    assert content != null;
                    content.setText(ss);
                }
                else{
                    ss.setSpan(fcsColor, 30, 30 + binding.getAccount().getNameSurname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    assert content != null;
                    content.setText(ss);
                }

                TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                assert positiveBtn != null;
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
    public void onBackPressed() {
        onBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status.getInstance().onConnect();
        getProfileInformation();
        clearNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status.getInstance().onDisconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(find != null){
            find.onDestroy();
        }
    }
}

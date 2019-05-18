package com.ibrhmdurna.chatapp.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.message.Image;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.models.Request;
import com.ibrhmdurna.chatapp.start.StartActivity;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.tooltip.OnDismissListener;
import com.tooltip.Tooltip;

public class MainActivity extends AppCompatActivity implements ViewComponentFactory {

    private FrameLayout mainFrame, fullFrame;
    private AppBarLayout mainAppBarLayout;
    private BottomNavigationView bottomNavigationView;
    private TextView messageNotFoundView, friendsNotFoundView, requestNotFoundView;
    private ImageButton searchBtn;
    private ImageButton writeBtn;
    private TextView appBarTitle;
    private TextView toolbarTitle;

    private String page;

    private View notificationBadge;
    private View chatBadge;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolsManagement();

        if(getIntent().getExtras() != null){
            page = getIntent().getStringExtra("page");

            if(page.equals("Main")){
                showFragment(new ChatsFragment(), "ChatsFragment");
            }
            else {
                showFragment(new AccountFragment(), "AccountFragment");
                bottomNavigationView.setSelectedItemId(R.id.account_item);
            }
        }
        else{
            showFragment(new ChatsFragment(), "ChatsFragment");
        }
    }

    private void chatListener(){
        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).removeEventListener(chatEventListener);
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).addValueEventListener(chatEventListener);
    }

    private void requestListener(){
        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).removeEventListener(requestEventListener);
        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).addValueEventListener(requestEventListener);
    }

    private ValueEventListener requestEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int count = 0;
            if(dataSnapshot.exists()){
                removeBadgeView();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Request request = snapshot.getValue(Request.class);

                    assert request != null;
                    if(!request.isSeen()){
                        count++;
                    }
                }

                if(count > 0){
                    addBadgeView(count);
                }
                else {
                    removeBadgeView();
                }
            }
            else {
                removeBadgeView();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener chatEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int count = 0;
            if(dataSnapshot.exists()){
                removeChatBadgeView();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(!chat.isSeen()){
                        count++;
                    }
                }

                if(count > 0){
                    addChatBadgeView(count);
                }
                else{
                    removeChatBadgeView();
                }

            }
            else {
                removeChatBadgeView();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @SuppressLint("SetTextI18n")
    private void addChatBadgeView(int count){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(0);

        chatBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_chat_badge, menuView, false);
        TextView text = chatBadge.findViewById(R.id.badge);
        text.setText(count + "");
        itemView.addView(chatBadge);
    }

    private void removeChatBadgeView(){
        if(chatBadge != null)
            chatBadge.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void addBadgeView(int count){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);

        notificationBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false);
        TextView text = notificationBadge.findViewById(R.id.badge);
        text.setText(count + "");
        itemView.addView(notificationBadge);
    }

    private void removeBadgeView(){
        if(notificationBadge != null)
            notificationBadge.setVisibility(View.GONE);
    }

    private void showFragment(Fragment fragment, String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if(tag .equals("AccountFragment"))
            transaction.replace(R.id.fullMainFrame, fragment);
        else
            transaction.replace(R.id.mainFrame, fragment);

        transaction.addToBackStack(tag);
        transaction.commit();
    }

    private void bottomViewItemSelected(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.chats_item:
                        showFragment(new ChatsFragment(), "ChatsFragment");
                        App.Background.getInstance().clearThisPage("ChatsFragment");
                        App.Background.getInstance().addPage("ChatsFragment");
                        toolbarTitle.setText(getString(R.string.chats));
                        appBarTitle.setText(getString(R.string.chats));
                        writeBtn.setVisibility(View.VISIBLE);
                        break;
                    case R.id.friends_item:
                        showFragment(new FriendsFragment(), "FriendsFragment");
                        App.Background.getInstance().clearThisPage("FriendsFragment");
                        App.Background.getInstance().addPage("FriendsFragment");
                        toolbarTitle.setText(getString(R.string.friends));
                        appBarTitle.setText(getString(R.string.friends));
                        writeBtn.setVisibility(View.GONE);
                        break;
                    case R.id.requests_item:
                        showFragment(new RequestsFragment(), "RequestsFragment");
                        App.Background.getInstance().clearThisPage("RequestsFragment");
                        App.Background.getInstance().addPage("RequestsFragment");
                        toolbarTitle.setText(getString(R.string.requests));
                        appBarTitle.setText(getString(R.string.requests));
                        writeBtn.setVisibility(View.GONE);
                        removeBadgeView();
                        break;
                    case R.id.account_item:
                        showFragment(new AccountFragment(), "AccountFragment");
                        App.Background.getInstance().clearThisPage("AccountFragment");
                        App.Background.getInstance().addPage("AccountFragment");
                        mainAppBarLayout.setVisibility(View.GONE);
                        fullFrame.setVisibility(View.VISIBLE);
                        mainFrame.setVisibility(View.GONE);
                        writeBtn.setVisibility(View.GONE);
                        return true;
                }

                mainAppBarLayout.setVisibility(View.VISIBLE);
                fullFrame.setVisibility(View.GONE);
                mainFrame.setVisibility(View.VISIBLE);
                messageNotFoundView.setVisibility(View.GONE);
                friendsNotFoundView.setVisibility(View.GONE);
                requestNotFoundView.setVisibility(View.GONE);
                return true;
            }
        });


        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.chats_item:
                        NestedScrollView nestedMessages = findViewById(R.id.nestedMessagesContainer);
                        nestedMessages.smoothScrollTo(0,0);
                        break;
                    case R.id.friends_item:
                        NestedScrollView nestedFriends = findViewById(R.id.nestedFriendsContainer);
                        nestedFriends.smoothScrollTo(0,0);
                        break;
                    case R.id.requests_item:
                        NestedScrollView nestedRequests = findViewById(R.id.nestedRequestsContainer);
                        nestedRequests.smoothScrollTo(0,0);
                        break;
                    case R.id.account_item:
                        NestedScrollView nestedAccount = findViewById(R.id.nestedAccountContainer);
                        nestedAccount.smoothScrollTo(0,0);
                        break;
                }
            }
        });

    }

    private void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public void buildView(){
        appBarTitle = findViewById(R.id.app_bar_title);
        mainFrame = findViewById(R.id.mainFrame);
        fullFrame = findViewById(R.id.fullMainFrame);
        toolbarTitle = findViewById(R.id.toolbar_title);
        bottomNavigationView = findViewById(R.id.mainBottomNavigationView);
        mainAppBarLayout = findViewById(R.id.appbar);
        messageNotFoundView = findViewById(R.id.messages_not_found_view);
        friendsNotFoundView = findViewById(R.id.friend_not_found_view);
        requestNotFoundView = findViewById(R.id.request_not_found_view);
        searchBtn = findViewById(R.id.toolbar_search_btn);
        writeBtn = findViewById(R.id.toolbar_write_btn);
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcessMain(this);
        buildView();
        bottomViewItemSelected();
    }

    @Override
    public void onBackPressed() {
        if(page.equals("Main")){
            if(App.Background.getInstance().pageStackChildCount() > 1){
                switch (App.Background.getInstance().getPageStackList().get(App.Background.getInstance().pageStackChildCount() - 2)){
                    case "ChatsFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.chats_item);
                        writeBtn.setVisibility(View.VISIBLE);
                        break;
                    case "FriendsFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.friends_item);
                        writeBtn.setVisibility(View.GONE);
                        break;
                    case "RequestsFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.requests_item);
                        writeBtn.setVisibility(View.GONE);
                        break;
                    case "AccountFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.account_item);
                        writeBtn.setVisibility(View.GONE);
                        break;
                    default:
                        finish();
                }
            }
            else if(App.Background.getInstance().pageStackChildCount() == 1){
                bottomNavigationView.setSelectedItemId(R.id.chats_item);
                App.Background.getInstance().clearAllPage();
                writeBtn.setVisibility(View.VISIBLE);
            }
            else {
                finish();
            }
        }
        else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToStart();
        }
        else{
            requestListener();
            chatListener();

            isBegin();
        }
    }

    private void isBegin(){
        SharedPreferences prefs = getSharedPreferences("START", MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        boolean isDialog = prefs.getBoolean("DARK_MODE_DIALOG", true);
        boolean isSearch = prefs.getBoolean("SEARCH_TOOLTIP", true);

        AlertDialog dialog = DialogController.getInstance().dialogDarkMode(this);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Tooltip tooltip = new Tooltip.Builder(searchBtn, R.style.CustomToolTip)
                        .setText(R.string.find_your_friend)
                        .setGravity(Gravity.BOTTOM)
                        .show();

                tooltip.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        editor.putBoolean("SEARCH_TOOLTIP", false);
                        editor.apply();
                    }
                });
            }
        });

        if(isDialog){
            dialog.show();
            editor.putBoolean("DARK_MODE_DIALOG", false);
            editor.apply();
        }
        else if(isSearch){
            Tooltip tooltip = new Tooltip.Builder(searchBtn, R.style.CustomToolTip)
                    .setText("Find your friend in the world of ChatApp!")
                    .setGravity(Gravity.BOTTOM)
                    .show();
            tooltip.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    editor.putBoolean("SEARCH_TOOLTIP", false);
                    editor.apply();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status.getInstance().onConnect();
        Insert.getInstance().deviceToken(uid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status.getInstance().onDisconnect();
    }

    @Override
    protected void onDestroy() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Firebase.getInstance().getDatabaseReference().child("Request").child(uid).removeEventListener(requestEventListener);
            Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).removeEventListener(chatEventListener);
        }
        super.onDestroy();
    }
}

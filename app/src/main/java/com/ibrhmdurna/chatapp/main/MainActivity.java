package com.ibrhmdurna.chatapp.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Connection;
import com.ibrhmdurna.chatapp.image.GalleryActivity;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.models.Request;
import com.ibrhmdurna.chatapp.start.StartActivity;
import com.ibrhmdurna.chatapp.util.controller.DialogController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewComponentFactory {

    private Toolbar toolbar;
    private FrameLayout mainFrame, fullFrame;
    private AppBarLayout mainAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private BottomNavigationView bottomNavigationView;
    private TextView messageNotFoundView, friendsNotFoundView, requestNotFoundView;

    private String page;

    private View notificationBadge;
    private View chatBadge;

    private String uid;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolsManagement();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        page = getIntent().getStringExtra("page");

        if(page.equals("Main")){
            showFragment(new ChatsFragment(), "ChatsFragment");
        }
        else {
            showFragment(new AccountFragment(), "AccountFragment");
            bottomNavigationView.setSelectedItemId(R.id.account_item);
        }
    }

    private void chatListener(){
        uid = FirebaseAuth.getInstance().getUid();

        databaseReference.child("Chats").child(uid).removeEventListener(chatEvenListener);
        databaseReference.child("Chats").child(uid).addValueEventListener(chatEvenListener);
    }

    private void requestListener(){
        uid = FirebaseAuth.getInstance().getUid();

        databaseReference.child("Request").child(uid).removeEventListener(requestEventListener);
        databaseReference.child("Request").child(uid).addValueEventListener(requestEventListener);
    }

    private ValueEventListener requestEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int count = 0;
            if(dataSnapshot.exists()){
                removeBadgeView();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Request request = snapshot.getValue(Request.class);

                    if(!request.isSeen()){
                        count++;
                    }
                }

                if(count > 0){
                    addBadgeView(count,2);
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

    private ValueEventListener chatEvenListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int count = 0;
            if(dataSnapshot.exists()){
                removeChatBadgeView();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(!chat.isSeen()){
                        count++;
                    }
                }

                if(count > 0){
                    addChatBadgeView(count, 0);
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
    private void addChatBadgeView(int count, int position){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(position);

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
    private void addBadgeView(int count, int position){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(position);

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
                    case R.id.messages_item:
                        showFragment(new ChatsFragment(), "ChatsFragment");
                        App.Background.getInstance().clearThisPage("ChatsFragment");
                        App.Background.getInstance().addPage("ChatsFragment");
                        collapsingToolbarLayout.setTitle("Chats");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.messages_main_menu);
                        break;
                    case R.id.friends_item:
                        showFragment(new FriendsFragment(), "FriendsFragment");
                        App.Background.getInstance().clearThisPage("FriendsFragment");
                        App.Background.getInstance().addPage("FriendsFragment");
                        collapsingToolbarLayout.setTitle("Friends");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        break;
                    case R.id.requests_item:
                        showFragment(new RequestsFragment(), "RequestsFragment");
                        App.Background.getInstance().clearThisPage("RequestsFragment");
                        App.Background.getInstance().addPage("RequestsFragment");
                        collapsingToolbarLayout.setTitle("Requests");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        removeBadgeView();
                        break;
                    case R.id.account_item:
                        showFragment(new AccountFragment(), "AccountFragment");
                        App.Background.getInstance().clearThisPage("AccountFragment");
                        App.Background.getInstance().addPage("AccountFragment");
                        mainAppBarLayout.setVisibility(View.GONE);
                        fullFrame.setVisibility(View.VISIBLE);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        return true;
                }

                mainAppBarLayout.setVisibility(View.VISIBLE);
                fullFrame.setVisibility(View.GONE);
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
                    case R.id.messages_item:
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
        toolbar = findViewById(R.id.mainToolbar);
        mainFrame = findViewById(R.id.mainFrame);
        fullFrame = findViewById(R.id.fullMainFrame);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        bottomNavigationView = findViewById(R.id.mainBottomNavigationView);
        mainAppBarLayout = findViewById(R.id.appbar);
        messageNotFoundView = findViewById(R.id.messages_not_found_view);
        friendsNotFoundView = findViewById(R.id.friend_not_found_view);
        requestNotFoundView = findViewById(R.id.request_not_found_view);
    }

    @Override
    public void toolsManagement(){
        buildView();
        bottomViewItemSelected();
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_item:
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.write_item:
                Intent writeIntent = new Intent(getApplicationContext(), WriteActivity.class);
                startActivity(writeIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages_main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(page.equals("Main")){
            if(App.Background.getInstance().pageStackChildCount() > 1){
                switch (App.Background.getInstance().getPageStackList().get(App.Background.getInstance().pageStackChildCount() - 2)){
                    case "ChatsFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.messages_item);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.messages_main_menu);
                        break;
                    case "FriendsFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.friends_item);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        break;
                    case "RequestsFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.requests_item);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        break;
                    case "AccountFragment":
                        App.Background.getInstance().removePage();
                        bottomNavigationView.setSelectedItemId(R.id.account_item);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        break;
                    default:
                        finish();
                }
            }
            else if(App.Background.getInstance().pageStackChildCount() == 1){
                bottomNavigationView.setSelectedItemId(R.id.messages_item);
                App.Background.getInstance().clearAllPage();
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.messages_main_menu);
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

            SharedPreferences prefs = getSharedPreferences("START", MODE_PRIVATE);
            boolean isStart = prefs.getBoolean("DARK_MODE_DIALOG", true);

            if(isStart){
                AlertDialog dialog = DialogController.getInstance().dialogDarkMode(this);
                dialog.show();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("DARK_MODE_DIALOG", false);
                editor.apply();
            }
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
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            databaseReference.child("Request").child(uid).removeEventListener(requestEventListener);
            databaseReference.child("Chats").child(uid).removeEventListener(chatEvenListener);
        }
        super.onDestroy();
    }
}

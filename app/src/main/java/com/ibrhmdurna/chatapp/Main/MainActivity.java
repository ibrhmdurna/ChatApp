package com.ibrhmdurna.chatapp.Main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.Local.ChatActivity;
import com.ibrhmdurna.chatapp.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout mainFrame, fullFrame;
    private AppBarLayout mainAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private BottomNavigationView bottomNavigationView;
    private TextView notFoundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolsManagement();

        showFragment(new MessagesFragment(), "MessagesFragment");
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
                        showFragment(new MessagesFragment(), "MessagesFragment");
                        App.Background.addPage("MessagesFragment");
                        collapsingToolbarLayout.setTitle("Messages");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.messages_main_menu);
                        break;
                    case R.id.friends_item:
                        showFragment(new FriendsFragment(), "FriendsFragment");
                        App.Background.addPage("FriendsFragment");
                        collapsingToolbarLayout.setTitle("Friends");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        break;
                    case R.id.requests_item:
                        showFragment(new RequestsFragment(), "RequestsFragment");
                        App.Background.addPage("RequestsFragment");
                        collapsingToolbarLayout.setTitle("Requests");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        break;
                    case R.id.account_item:
                        showFragment(new AccountFragment(), "AccountFragment");
                        App.Background.addPage("AccountFragment");
                        mainAppBarLayout.setVisibility(View.GONE);
                        fullFrame.setVisibility(View.VISIBLE);
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.main_menu);
                        return true;
                }

                mainAppBarLayout.setVisibility(View.VISIBLE);
                fullFrame.setVisibility(View.GONE);
                notFoundView.setVisibility(View.GONE);
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

    private void buildView(){
        toolbar = findViewById(R.id.mainToolbar);
        mainFrame = findViewById(R.id.mainFrame);
        fullFrame = findViewById(R.id.fullMainFrame);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        bottomNavigationView = findViewById(R.id.mainBottomNavigationView);
        mainAppBarLayout = findViewById(R.id.appbar);
        notFoundView = findViewById(R.id.not_found_view);
    }

    private void toolsManagement(){
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
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(chatIntent);
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
        if(App.Background.pageStackChildCount() > 1){
            switch (App.Background.getPageStackList().get(App.Background.pageStackChildCount() - 2)){
                case "MessagesFragment":
                    App.Background.removePage();
                    bottomNavigationView.setSelectedItemId(R.id.messages_item);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.messages_main_menu);
                    break;
                case "FriendsFragment":
                    App.Background.removePage();
                    bottomNavigationView.setSelectedItemId(R.id.friends_item);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.main_menu);
                    break;
                case "RequestsFragment":
                    App.Background.removePage();
                    bottomNavigationView.setSelectedItemId(R.id.requests_item);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.main_menu);
                    break;
                case "AccountFragment":
                    App.Background.removePage();
                    bottomNavigationView.setSelectedItemId(R.id.account_item);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.main_menu);
                    break;
                default:
                    finish();
            }
        }
        else if(App.Background.pageStackChildCount() == 1){
            bottomNavigationView.setSelectedItemId(R.id.messages_item);
            App.Background.getPageStackList().clear();
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.messages_main_menu);
        }
        else {
            finish();
        }
    }
}

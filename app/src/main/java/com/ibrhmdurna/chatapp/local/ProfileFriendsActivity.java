package com.ibrhmdurna.chatapp.local;

import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.AccountFriendFindAll;
import com.ibrhmdurna.chatapp.database.find.MutualFriendFindAll;
import com.ibrhmdurna.chatapp.util.Environment;

public class ProfileFriendsActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private EditText searchInput;
    private ImageButton searchClearView;
    private TabLayout tabLayout;
    private NestedScrollView nestedContainer;
    private ImageButton upAttackBtn;

    private String uid;

    private AbstractFind find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);

        uid = getIntent().getStringExtra("user_id");

        toolsManagement();
    }

    private void getFriends(){
        find = new Find(new AccountFriendFindAll(this, uid));
        find.getContent();
    }

    private void getMutual(){
        find = new Find(new MutualFriendFindAll(this, uid));
        find.getContent();
    }

    private void tabProcess(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getFriends();
                        break;
                    case 1:
                        getMutual();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void inputProcess(){
        searchInput.addTextChangedListener(inputWatcher);
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(searchInput.getText().length() > 0){
                searchClearView.setVisibility(View.VISIBLE);
            }
            else {
                searchClearView.setVisibility(View.GONE);
            }
        }
    };

    private void scrollListener(){
        nestedContainer.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_down);
                    upAttackBtn.setAnimation(fadeOut);
                    upAttackBtn.setVisibility(View.GONE);
                }
                else if (scrollY > 1){
                    if(upAttackBtn.getVisibility() == View.GONE){
                        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_down);
                        upAttackBtn.setAnimation(fadeIn);
                        upAttackBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        upAttackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedContainer.smoothScrollTo(0,0);
            }
        });
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcessSearchBar(this);
        buildView();
        scrollListener();
        inputProcess();
        tabProcess();
        getFriends();
    }

    @Override
    public void buildView() {
        searchInput = findViewById(R.id.search_input);
        searchClearView = findViewById(R.id.clear_search_btn);
        tabLayout = findViewById(R.id.bottom_tab_selected);
        nestedContainer = findViewById(R.id.nestedContainer);
        upAttackBtn = findViewById(R.id.up_attack_btn);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clear_search_btn) {
            searchInput.getText().clear();
            searchClearView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status.getInstance().onConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status.getInstance().onDisconnect();
    }

    @Override
    protected void onDestroy() {
        if(find != null){
            find.onDestroy();
        }
        super.onDestroy();
    }
}

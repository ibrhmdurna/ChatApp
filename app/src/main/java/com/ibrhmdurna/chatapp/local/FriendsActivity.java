package com.ibrhmdurna.chatapp.local;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Connection;
import com.ibrhmdurna.chatapp.util.Environment;
import com.ibrhmdurna.chatapp.util.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private Toolbar toolbar;
    private TextView friendsTitle;
    private LinearLayout searchInputLayout;
    private EditText searchInput;
    private ImageButton searchClearView;
    private NestedScrollView noFriendsView;

    private RelativeLayout contentView, bottomView;

    private boolean isAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        toolsManagement();

        isAccount = getIntent().getBooleanExtra("isAccount", true);

        if(isAccount){
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)contentView.getLayoutParams();
            params.setMargins(0,0,0,0);
            contentView.setLayoutParams(params);
            bottomView.setVisibility(View.GONE);
        }
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

    @Override
    public void buildView(){
        toolbar = findViewById(R.id.friends_toolbar);
        friendsTitle = findViewById(R.id.friends_title_view);
        searchInputLayout = findViewById(R.id.friends_search_layout);
        searchInput = findViewById(R.id.friends_search_input);
        searchClearView = findViewById(R.id.clear_search_btn);
        noFriendsView = findViewById(R.id.no_friends_view);
        contentView = findViewById(R.id.content_view);
        bottomView = findViewById(R.id.bottom_view);
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcess(this, R.id.friends_toolbar);
        buildView();
        inputProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.write_search_item){
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(500);
            searchInputLayout.setAnimation(fadeIn);
            friendsTitle.setVisibility(View.VISIBLE);
            searchInputLayout.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
            searchInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }
        else if(searchInputLayout.getVisibility() == View.VISIBLE) {
            friendsTitle.setVisibility(View.GONE);
            searchInputLayout.setVisibility(View.GONE);
            getMenuInflater().inflate(R.menu.extra_menu, toolbar.getMenu());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
        else {
            super.onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.extra_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_search_btn:
                searchInput.getText().clear();
                searchClearView.setVisibility(View.GONE);
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
}

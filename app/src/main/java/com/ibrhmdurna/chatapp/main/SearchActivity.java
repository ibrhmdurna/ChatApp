package com.ibrhmdurna.chatapp.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.ybq.android.spinkit.SpinKitView;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Search;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFindAll;
import com.ibrhmdurna.chatapp.database.bridge.FindAll;
import com.ibrhmdurna.chatapp.database.findAll.RecentFindAll;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private EditText searchInput;
    private ImageButton clearView;

    private NestedScrollView noRecentLayout;
    private NestedScrollView recentLayout;
    private NestedScrollView searchLayout;

    private RecyclerView recentView;
    private RecyclerView searchView;

    private SpinKitView loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolsManagement();
    }

    private void recentProcess(){
        AbstractFindAll recentFindAll = new FindAll(new RecentFindAll(this, recentView, noRecentLayout, recentLayout));
        recentFindAll.getInformation();
    }

    private void searchProcess(){
        final List<Account> accountList = new ArrayList<>();
        final SearchAdapter searchAdapter = new SearchAdapter(this, accountList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchView.setLayoutManager(layoutManager);
        searchView.setAdapter(searchAdapter);
        searchView.setHasFixedSize(true);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(searchInput.getText().toString().trim().length() > 0){
                    noRecentLayout.setVisibility(View.GONE);
                    recentLayout.setVisibility(View.GONE);
                    Search.getInstance().searchAccount(searchInput.getText().toString().trim(), searchAdapter, accountList, searchLayout, loadingBar);
                }
                else {
                    searchLayout.setVisibility(View.GONE);
                    accountList.clear();
                    searchAdapter.notifyDataSetChanged();
                    recentProcess();
                }
            }
        });
    }

    private TextWatcher checkSearch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(searchInput.getText().length() > 0)
                clearView.setVisibility(View.VISIBLE);
            else
                clearView.setVisibility(View.GONE);
        }
    };

    private void inputWatcherProcess(){
        searchInput.addTextChangedListener(checkSearch);
    }

    private void toolbarProcess(){
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recentProcess();
    }

    @Override
    public void buildView(){
        searchInput = findViewById(R.id.search_input);
        clearView = findViewById(R.id.clear_search_btn);
        noRecentLayout = findViewById(R.id.no_recent_layout);
        recentLayout = findViewById(R.id.recent_layout);
        searchLayout = findViewById(R.id.search_layout);
        searchView = findViewById(R.id.search_container);
        recentView = findViewById(R.id.recent_container);
        loadingBar = findViewById(R.id.search_loading_bar);
    }

    @Override
    public void toolsManagement(){
        buildView();
        toolbarProcess();
        inputWatcherProcess();
        searchInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        searchProcess();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_search_btn:
                searchInput.getText().clear();
                break;
        }
    }
}

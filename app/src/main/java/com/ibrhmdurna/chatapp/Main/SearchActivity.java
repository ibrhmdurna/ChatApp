package com.ibrhmdurna.chatapp.Main;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText searchInput;
    private ImageButton clearView;

    private NestedScrollView recentView;

    private RecyclerView recentRecylerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolsManagement();

        searchInput.requestFocus();
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

    private void buildView(){
        searchInput = findViewById(R.id.search_input);
        clearView = findViewById(R.id.clear_search_btn);
        recentView = findViewById(R.id.recent_layout);
        recentRecylerView = findViewById(R.id.recent_container);
    }

    private void toolsManagement(){
        buildView();
        toolbarProcess();
        inputWatcherProcess();
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

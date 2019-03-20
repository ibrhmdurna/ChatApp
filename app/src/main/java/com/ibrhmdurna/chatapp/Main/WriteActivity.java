package com.ibrhmdurna.chatapp.Main;

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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.Environment;
import com.ibrhmdurna.chatapp.Utils.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView writeTitle;
    private LinearLayout searchInputLayout;
    private EditText searchInput;
    private ImageButton searchClearView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        toolsManagement();

        List<String> list = new ArrayList<>();

        for(int i = 0; i < 20; i++){
            list.add("Write " + i);
        }

        RecyclerView recyclerView = findViewById(R.id.write_container);
        MessagesAdapter adapter = new MessagesAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
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

    private void buildView(){
        toolbar = findViewById(R.id.write_toolbar);
        writeTitle = findViewById(R.id.write_title_view);
        searchInputLayout = findViewById(R.id.write_search_layout);
        searchInput = findViewById(R.id.search_input);
        searchClearView = findViewById(R.id.clear_search_btn);
    }

    private void toolsManagement() {
        Environment.toolbarProcess(this, R.id.write_toolbar);
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
            writeTitle.setVisibility(View.VISIBLE);
            searchInputLayout.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
            searchInput.requestFocus();
        }
        else if(searchInputLayout.getVisibility() == View.VISIBLE) {
            writeTitle.setVisibility(View.GONE);
            searchInputLayout.setVisibility(View.GONE);
            getMenuInflater().inflate(R.menu.write_menu, toolbar.getMenu());
        }
        else {
            super.onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);
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
}

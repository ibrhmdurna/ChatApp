package com.ibrhmdurna.chatapp.main;

import android.content.Intent;
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

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.findAll.WriteFindAll;
import com.ibrhmdurna.chatapp.util.Environment;

public class WriteActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private EditText searchInput;
    private ImageButton searchClearView;
    private NestedScrollView nestedContainer;
    private ImageButton upAttackBtn;

    private AbstractFind find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        toolsManagement();
    }

    private void getFriends(){
        find = new Find(new WriteFindAll(this));
        find.getContent();
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
    public void buildView(){
        searchInput = findViewById(R.id.search_input);
        searchClearView = findViewById(R.id.clear_search_btn);
        nestedContainer = findViewById(R.id.nestedContainer);
        upAttackBtn = findViewById(R.id.up_attack_btn);
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcessSearchBar(this);
        buildView();
        scrollListener();
        inputProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_search_btn:
                searchInput.getText().clear();
                searchClearView.setVisibility(View.GONE);
                break;
            case R.id.write_add_friend_layout:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status.getInstance().onConnect();
        getFriends();
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

package com.ibrhmdurna.chatapp.local;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Status;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.findAll.AccountFriendFindAll;
import com.ibrhmdurna.chatapp.util.Environment;

public class FriendsActivity extends AppCompatActivity implements ViewComponentFactory, View.OnClickListener {

    private EditText searchInput;
    private ImageButton searchClearView;

    private AbstractFind find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        toolsManagement();
    }

    private void getFriends(){
        find = new Find(new AccountFriendFindAll(this, FirebaseAuth.getInstance().getUid()));
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

    @Override
    public void buildView(){
        searchInput = findViewById(R.id.search_input);
        searchClearView = findViewById(R.id.clear_search_btn);
    }

    @Override
    public void toolsManagement() {
        Environment.getInstance().toolbarProcessSearchBar(this);
        buildView();
        inputProcess();
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

package com.ibrhmdurna.chatapp.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.findAll.BlockFindAll;
import com.ibrhmdurna.chatapp.util.Environment;

public class BlockedAccountsActivity extends AppCompatActivity implements ViewComponentFactory {

    private AbstractFind find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_accounts);

        toolsManagement();
    }

    private void getBlocks(){
        find = new Find(new BlockFindAll(this));
        find.getContent();
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }

    @Override
    public void toolsManagement(){
        Environment.getInstance().toolbarProcess(this);
        getBlocks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(find != null){
            find.onDestroy();
        }
    }
}

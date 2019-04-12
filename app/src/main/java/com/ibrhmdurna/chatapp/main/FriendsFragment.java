package com.ibrhmdurna.chatapp.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFindAll;
import com.ibrhmdurna.chatapp.database.bridge.FindAll;
import com.ibrhmdurna.chatapp.database.findAll.FriendFindAll;
import com.ibrhmdurna.chatapp.database.findAll.OnlineFindAll;
import com.ibrhmdurna.chatapp.util.adapter.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements ViewComponentFactory {

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        toolsManagement();

        return view;
    }

    private void getOnline(){
        AbstractFindAll findAll = new FindAll(new OnlineFindAll(this));
        findAll.getInformation();
    }

    private void getFriends(){
        AbstractFindAll findAll = new FindAll(new FriendFindAll(this));
        findAll.getInformation();
    }

    @Override
    public void onStart() {
        super.onStart();
        getFriends();
        getOnline();
    }

    @Override
    public void toolsManagement() {
        buildView();
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }
}

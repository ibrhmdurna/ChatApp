package com.ibrhmdurna.chatapp.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.findAll.FriendFindAll;
import com.ibrhmdurna.chatapp.database.findAll.OnlineFindAll;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements ViewComponentFactory {

    private AbstractFind find;

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
        find = new Find(new OnlineFindAll(this));
        find.getContent();
    }

    private void getFriends(){
        find = new Find(new FriendFindAll(this));
        find.getContent();
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

    @Override
    public void onDestroy() {
        find.onDestroy();
        super.onDestroy();
    }
}

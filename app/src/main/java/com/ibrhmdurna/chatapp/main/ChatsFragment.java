package com.ibrhmdurna.chatapp.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFindAll;
import com.ibrhmdurna.chatapp.database.bridge.FindAll;
import com.ibrhmdurna.chatapp.database.findAll.ChatFindAll;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment implements ViewComponentFactory {

    private View view;

    private AbstractFindAll findAll;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(getContext());
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        toolsManagement();

        return view;
    }

    private void getChats(){
        findAll = new FindAll(new ChatFindAll(this));
        findAll.getContent();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            getChats();
        }
    }

    @Override
    public void toolsManagement() {
        // ---- COMPONENT ----
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }

    @Override
    public void onDestroy() {
        if(findAll != null){
            findAll.onDestroy();
        }
        super.onDestroy();
    }
}

package com.ibrhmdurna.chatapp.main;


import android.app.NotificationManager;
import android.content.Context;
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

    public static boolean isChat = false;

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
    public void onResume() {
        super.onResume();

        isChat = true;

        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }

    @Override
    public void onPause() {
        super.onPause();
        isChat = false;
    }

    @Override
    public void onDestroy() {
        if(findAll != null){
            findAll.onDestroy();
        }
        isChat = false;
        super.onDestroy();
    }
}

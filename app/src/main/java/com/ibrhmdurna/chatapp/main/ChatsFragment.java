package com.ibrhmdurna.chatapp.main;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.findAll.ChatFindAll;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment implements ViewComponentFactory {

    private View view;

    private AbstractFind find;

    public static boolean isChat = false;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(getContext());
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        toolsManagement();

        return view;
    }

    private void getChats(){
        find = new Find(new ChatFindAll(this));
        find.getContent();
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

        NotificationManager manager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        isChat = false;
    }

    @Override
    public void onDestroy() {
        if(find != null){
            find.onDestroy();
        }
        isChat = false;
        super.onDestroy();
    }
}

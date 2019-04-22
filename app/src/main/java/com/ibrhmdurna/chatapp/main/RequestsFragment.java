package com.ibrhmdurna.chatapp.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Update;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFindAll;
import com.ibrhmdurna.chatapp.database.bridge.FindAll;
import com.ibrhmdurna.chatapp.database.findAll.RequestFindAll;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment implements ViewComponentFactory {

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        toolsManagement();

        return view;
    }

    private void getRequest(){
        AbstractFindAll findAll = new FindAll(new RequestFindAll(getView()));
        findAll.getContent();
    }

    @Override
    public void onStart() {
        super.onStart();
        getRequest();
        Update.getInstance().seenAllRequest();
    }

    @Override
    public void toolsManagement() {
        buildView();
    }

    @Override
    public void buildView() {
        // ------ COMPONENT -----
    }
}

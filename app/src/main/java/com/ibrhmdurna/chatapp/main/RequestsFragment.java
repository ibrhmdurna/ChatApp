package com.ibrhmdurna.chatapp.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.util.adapter.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment implements ViewComponentFactory {

    private View view;

    private List<String> list;
    private MessagesAdapter messagesAdapter;
    private RecyclerView recyclerView;
    private TextView notFoundView;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(getContext());
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        list = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(list);
        recyclerView = view.findViewById(R.id.requestsContainer);
        notFoundView = getActivity().findViewById(R.id.not_found_view);


        /*
        for (int i = 0; i < 10; i++){
            list.add("Request " + i);
        }
        */

        if(list.size() == 0){
            recyclerView.setVisibility(View.GONE);
            notFoundView.setVisibility(View.VISIBLE);
            notFoundView.setText("No Requests");
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            notFoundView.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messagesAdapter);

        messagesAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void toolsManagement() {
        // ---- COMPONENT ----
    }

    @Override
    public void buildView() {
        // ---- COMPONENT ----
    }
}

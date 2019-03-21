package com.ibrhmdurna.chatapp.Main;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private View view;

    private List<String> list;
    private MessagesAdapter messagesAdapter;
    private RecyclerView recyclerView;

    private TextView notFoundView;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getTheme(getContext());
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        list = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(list);
        recyclerView = view.findViewById(R.id.messagesContainer);
        notFoundView = getActivity().findViewById(R.id.not_found_view);

        /*
        for (int i = 0; i < 20; i++){
            list.add("Messages " + i);
        }
        */

        if(list.size() == 0){
            recyclerView.setVisibility(View.GONE);
            notFoundView.setVisibility(View.VISIBLE);
            notFoundView.setText("No Messages");
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
}

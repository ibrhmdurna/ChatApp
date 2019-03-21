package com.ibrhmdurna.chatapp.Main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Utils.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private View view;

    private List<String> list, list2;
    private MessagesAdapter messagesAdapter, messagesAdapter2;
    private RecyclerView onlineView, friendsView;
    private TextView notFoundView;
    private LinearLayout rootView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getTheme(getContext());
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(list);
        messagesAdapter2 = new MessagesAdapter(list2);
        onlineView = view.findViewById(R.id.online_container);
        friendsView = view.findViewById(R.id.friends_container);
        rootView = view.findViewById(R.id.root_view);
        notFoundView = getActivity().findViewById(R.id.not_found_view);

        /*
        for (int i = 0; i < 5; i++){
            list.add("Online " + i);
        }


        for (int i = 0; i < 10; i++){
            list2.add("Friends " + i);
        }
        */

        if(list2.size() == 0){
            rootView.setVisibility(View.GONE);
            notFoundView.setVisibility(View.VISIBLE);
            notFoundView.setText("No Friends");
        }
        else {
            rootView.setVisibility(View.VISIBLE);
            notFoundView.setVisibility(View.GONE);
        }

        onlineView.setLayoutManager(new LinearLayoutManager(getActivity()));
        onlineView.setHasFixedSize(true);
        onlineView.setAdapter(messagesAdapter);

        friendsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsView.setHasFixedSize(true);
        friendsView.setAdapter(messagesAdapter2);

        messagesAdapter.notifyDataSetChanged();
        messagesAdapter2.notifyDataSetChanged();

        return view;
    }
}

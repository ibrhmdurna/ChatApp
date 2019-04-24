package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.adapter.FriendAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AccountFriendFindAll implements IFind {

    private Activity context;

    private List<Friend> friendList;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private NestedScrollView notFoundView;
    private EditText searchInput;
    private RelativeLayout bottomLayout;

    private String uid;

    public AccountFriendFindAll(Activity context, String uid){
        this.context = context;
        this.uid = uid;
        buildView();
    }

    @Override
    public void buildView() {
        friendView = context.findViewById(R.id.friends_container);
        notFoundView = context.findViewById(R.id.no_friends_view);
        searchInput = context.findViewById(R.id.friends_search_input);
        if(!uid.equals(FirebaseAuth.getInstance().getUid())){
            bottomLayout = context.findViewById(R.id.bottom_layout);
        }
    }

    @Override
    public void getContent() {
        friendList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        friendAdapter = new FriendAdapter(context, friendList, 1);
        friendView.setLayoutManager(layoutManager);
        friendView.setAdapter(friendAdapter);

        FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();
                if(dataSnapshot.exists()){
                    friendView.setVisibility(View.VISIBLE);
                    notFoundView.setVisibility(View.GONE);
                    if(bottomLayout != null)
                        bottomLayout.setVisibility(View.VISIBLE);

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        final String friend_id = snapshot.getKey();

                        final Friend friend = snapshot.getValue(Friend.class);

                        FirebaseDatabase.getInstance().getReference().child("Accounts").child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final Account account = dataSnapshot.getValue(Account.class);
                                    account.setUid(dataSnapshot.getKey());
                                    friend.setAccount(account);
                                    friendList.add(friend);

                                    friendAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    friendView.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                    if(bottomLayout != null)
                        bottomLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getMore();
    }

    @Override
    public void getMore() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        final String myUid = FirebaseAuth.getInstance().getUid();

        friendAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String uid) {
                if(myUid.equals(uid)){
                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.putExtra("page", "Account");
                    context.startActivity(mainIntent);
                }
                else{
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("user_id", uid);
                    context.startActivity(profileIntent);
                }
            }
        });
    }

    private void filter(String text){
        List<Friend> filterList = new ArrayList<>();

        for(Friend friend : friendList){
            if(friend.getAccount().getNameSurname().toLowerCase().contains(text.toLowerCase())){
                filterList.add(friend);
            }
        }

        if(filterList.size() > 0){
            friendView.setVisibility(View.VISIBLE);
            notFoundView.setVisibility(View.GONE);
        }
        else{
            friendView.setVisibility(View.GONE);
            notFoundView.setVisibility(View.VISIBLE);
        }

        friendAdapter.filterList(filterList);
    }
}

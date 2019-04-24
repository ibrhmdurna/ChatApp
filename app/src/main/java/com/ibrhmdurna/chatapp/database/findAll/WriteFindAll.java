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
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.local.ChatActivity;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.adapter.FriendAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WriteFindAll implements IFind {

    private Activity context;

    private List<Friend> friendList;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private LinearLayout writeLayout;
    private NestedScrollView notFoundView;
    private EditText searchInput;
    private LinearLayout newGroupLayout;
    private LinearLayout addFriendLayout;

    public WriteFindAll(Activity context){
        this.context = context;
        buildView();
    }

    @Override
    public void buildView() {
        friendView = context.findViewById(R.id.write_container);
        writeLayout = context.findViewById(R.id.write_layout);
        notFoundView = context.findViewById(R.id.no_write_view);
        searchInput = context.findViewById(R.id.search_input);
        newGroupLayout = context.findViewById(R.id.write_new_group_layout);
        addFriendLayout = context.findViewById(R.id.write_add_friend_layout);
    }

    @Override
    public void getContent() {
        friendList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        friendAdapter = new FriendAdapter(context, friendList, 0);
        friendView.setLayoutManager(layoutManager);
        friendView.setAdapter(friendAdapter);

        String uid = FirebaseAuth.getInstance().getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        databaseReference.child("Friends").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();
                if(dataSnapshot.exists()){
                    writeLayout.setVisibility(View.VISIBLE);
                    notFoundView.setVisibility(View.GONE);

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        final String friend_id = snapshot.getKey();

                        final Friend friend = snapshot.getValue(Friend.class);

                        databaseReference.child("Accounts").child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final Account account = dataSnapshot.getValue(Account.class);
                                    account.setUid(dataSnapshot.getKey());
                                    friend.setAccount(account);
                                    friendList.add(friend);

                                    sortArrayList();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    writeLayout.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
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

                if(searchInput.getText().toString().length() > 0){
                    newGroupLayout.setVisibility(View.GONE);
                    addFriendLayout.setVisibility(View.GONE);
                }
                else{
                    newGroupLayout.setVisibility(View.VISIBLE);
                    addFriendLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        friendAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String uid) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("user_id", uid);
                context.startActivity(chatIntent);
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

    private void sortArrayList(){
        Collections.sort(friendList, new Comparator<Friend>() {
            @Override
            public int compare(Friend o1, Friend o2) {
                return o2.getAccount().getName().compareTo(o1.getAccount().getName());
            }
        });

        friendAdapter.notifyDataSetChanged();
    }
}

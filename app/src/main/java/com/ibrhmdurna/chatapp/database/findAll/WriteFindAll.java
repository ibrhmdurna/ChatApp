package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.local.ChatActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.adapter.FriendAdapter;
import com.ibrhmdurna.chatapp.util.adapter.WriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class WriteFindAll implements IFind {

    private Activity context;

    private List<Friend> friendList;
    private List<String> friendIds;
    private WriteAdapter writeAdapter;
    private RecyclerView friendView;
    private LinearLayout writeLayout;
    private NestedScrollView notFoundView;
    private EditText searchInput;
    private LinearLayout newGroupLayout;
    private LinearLayout addFriendLayout;

    private String uid;

    public WriteFindAll(Activity context){
        this.context = context;
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
        friendIds = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        writeAdapter = new WriteAdapter(context, friendList);
        friendView.setLayoutManager(layoutManager);
        friendView.setAdapter(writeAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).addChildEventListener(contentEventListener);

        getMore();
    }

    @Override
    public void getMore() {
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).addValueEventListener(moreEventListener);

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

        writeAdapter.setOnItemClickListener(new WriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String uid) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("user_id", uid);
                context.startActivity(chatIntent);
                context.finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(moreEventListener);
    }

    private ValueEventListener moreEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                writeLayout.setVisibility(View.VISIBLE);
                notFoundView.setVisibility(View.GONE);
            }
            else{
                writeLayout.setVisibility(View.GONE);
                notFoundView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ChildEventListener contentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists()){
                final Friend friend = dataSnapshot.getValue(Friend.class);

                Firebase.getInstance().getDatabaseReference().child("Accounts").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Account account = dataSnapshot.getValue(Account.class);
                            account.setUid(dataSnapshot.getKey());
                            friend.setAccount(account);
                            friendList.add(friend);
                            friendIds.add(dataSnapshot.getKey());

                            writeAdapter.notifyItemInserted(friendList.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            final Friend friend = dataSnapshot.getValue(Friend.class);
            final int index = friendIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                Firebase.getInstance().getDatabaseReference().child("Accounts").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Account account = dataSnapshot.getValue(Account.class);
                            account.setUid(dataSnapshot.getKey());
                            friend.setAccount(account);
                            friendList.set(index, friend);
                            writeAdapter.notifyItemChanged(index);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = friendIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                friendIds.remove(index);
                friendList.remove(index);
                writeAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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

        writeAdapter.filterList(filterList);
    }
}

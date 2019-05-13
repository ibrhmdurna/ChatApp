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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.adapter.FriendAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MutualFriendFindAll implements IFind {

    private Activity context;

    private List<Friend> friendList;
    private List<String> friendIds;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private NestedScrollView notFoundView;
    private EditText searchInput;

    private String uid;
    private String myUid;

    public MutualFriendFindAll(Activity context, String uid){
        this.context = context;
        this.uid = uid;
    }

    @Override
    public void buildView() {
        friendView = context.findViewById(R.id.friends_container);
        notFoundView = context.findViewById(R.id.no_friends_view);
        searchInput = context.findViewById(R.id.friends_search_input);
    }

    @Override
    public void getContent() {
        friendList = new ArrayList<>();
        friendIds = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        friendAdapter = new FriendAdapter(context, friendList, 1);
        friendView.setLayoutManager(layoutManager);
        friendView.setItemAnimator(null);
        friendView.setAdapter(friendAdapter);

        myUid = FirebaseAuth.getInstance().getUid();

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
            }
        });

        myUid = FirebaseAuth.getInstance().getUid();

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

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(moreEventListener);
    }

    private ValueEventListener moreEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                final int[] mutual = {0};
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Firebase.getInstance().getDatabaseReference().child("Friends").child(myUid).child(Objects.requireNonNull(snapshot.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                mutual[0]++;
                            }

                            if(mutual[0] > 0){
                                friendView.setVisibility(View.VISIBLE);
                                notFoundView.setVisibility(View.GONE);
                            }
                            else{
                                friendView.setVisibility(View.GONE);
                                notFoundView.setVisibility(View.VISIBLE);
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
                Firebase.getInstance().getDatabaseReference().child("Friends").child(myUid).child(Objects.requireNonNull(dataSnapshot.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final Friend mutualFriend = dataSnapshot.getValue(Friend.class);

                            Account account = new Account();
                            if(mutualFriend != null){
                                account.setUid(dataSnapshot.getKey());
                                mutualFriend.setAccount(account);
                                friendList.add(mutualFriend);
                                friendIds.add(dataSnapshot.getKey());

                                friendAdapter.notifyItemInserted(friendList.size() - 1);
                            }
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
            if(index > -1 && friend != null){
                Account account = new Account();
                account.setUid(dataSnapshot.getKey());
                friend.setAccount(account);
                friendList.set(index, friend);
                friendAdapter.notifyItemChanged(index);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = friendIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                friendIds.remove(index);
                friendList.remove(index);
                friendAdapter.notifyItemRemoved(index);
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

        friendAdapter.filterList(filterList);
    }
}

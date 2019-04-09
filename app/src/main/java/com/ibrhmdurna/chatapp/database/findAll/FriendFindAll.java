package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.adapter.FriendAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendFindAll implements IFind {

    private Activity context;

    private List<Friend> friendList;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private TextView notFoundView;
    private LinearLayout friendLayout;

    public FriendFindAll(Activity context, RecyclerView friendView, TextView notFoundView, LinearLayout friendLayout) {
        this.context = context;
        this.friendView = friendView;
        this.notFoundView = notFoundView;
        this.friendLayout = friendLayout;
    }

    @Override
    public void getInformation() {
        friendList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        friendAdapter = new FriendAdapter(context, friendList);
        friendView.setLayoutManager(layoutManager);
        friendView.setAdapter(friendAdapter);

        String uid = FirebaseAuth.getInstance().getUid();

        notFoundView.setVisibility(View.VISIBLE);
        notFoundView.setText("No Friends");

        FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    notFoundView.setVisibility(View.GONE);
                    final String friend_id = dataSnapshot.getKey();

                    final Friend friend = dataSnapshot.getValue(Friend.class);

                    FirebaseDatabase.getInstance().getReference().child("Accounts").child(friend_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Account account = dataSnapshot.getValue(Account.class);
                                account.setUid(dataSnapshot.getKey());
                                friend.setAccount(account);

                                if(!friend.getAccount().isOnline()){
                                    friendList.add(friend);
                                }
                                else {
                                    friendList.remove(friend);
                                }

                                if(friendList.size() > 0){
                                    friendLayout.setVisibility(View.VISIBLE);
                                }
                                else {
                                    friendLayout.setVisibility(View.GONE);
                                }

                                sortArrayList();
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

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Long time = (Long) dataSnapshot.child("time").getValue();
                int position = findPosition(time);
                if(position != -1){
                    friendList.remove(position);
                    friendAdapter.notifyItemRemoved(position);
                }

                if(friendList.size() <= 0){
                    friendLayout.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                    notFoundView.setText("No Friends");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int findPosition(long time){
        for(int i = 0; i < friendList.size(); i++){
            if(friendList.get(i).getTime() == time){
                return i;
            }
        }
        return -1;
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

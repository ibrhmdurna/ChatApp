package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class OnlineFindAll implements IFind {

    private Activity context;

    private List<Friend> friendList;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private LinearLayout onlineLayout;

    public OnlineFindAll(Activity context, RecyclerView friendView, LinearLayout onlineLayout) {
        this.context = context;
        this.friendView = friendView;
        this.onlineLayout = onlineLayout;
    }

    @Override
    public void getInformation() {
        friendList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        friendAdapter = new FriendAdapter(context, friendList);
        friendView.setLayoutManager(layoutManager);
        friendView.setAdapter(friendAdapter);

        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    final String friend_id = dataSnapshot.getKey();

                    final Friend friend = dataSnapshot.getValue(Friend.class);

                    FirebaseDatabase.getInstance().getReference().child("Accounts").child(friend_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Account account = dataSnapshot.getValue(Account.class);
                                account.setUid(friend_id);
                                friend.setAccount(account);

                                if(friend.getAccount().isOnline()){
                                    friendList.add(friend);
                                }
                                else {
                                    friendList.remove(friend);
                                }

                                if(friendList.size() > 0){
                                    onlineLayout.setVisibility(View.VISIBLE);
                                }
                                else {
                                    onlineLayout.setVisibility(View.GONE);
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

                /*
                if(friendList.size() <= 0){
                    onlineLayout.setVisibility(View.GONE);
                }*/
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

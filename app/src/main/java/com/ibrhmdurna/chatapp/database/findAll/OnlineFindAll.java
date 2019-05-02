package com.ibrhmdurna.chatapp.database.findAll;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Friend;
import com.ibrhmdurna.chatapp.util.adapter.FriendAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OnlineFindAll implements IFind {

    private Fragment context;

    private List<Friend> friendList;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private LinearLayout onlineLayout;

    private String uid;

    public OnlineFindAll(Fragment context) {
        this.context = context;
        buildView();
    }

    @Override
    public void buildView() {
        friendView = context.getView().findViewById(R.id.online_container);
        onlineLayout = context.getView().findViewById(R.id.online_layout);
    }

    @Override
    public void getContent() {
        friendList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        friendAdapter = new FriendAdapter(context.getContext(), friendList, 0);
        friendView.setLayoutManager(layoutManager);
        friendView.setAdapter(friendAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).addListenerForSingleValueEvent(contentEventListener);

        getMore();
    }

    @Override
    public void getMore() {
        friendAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String uid) {
                Intent profileIntent = new Intent(context.getActivity(), ProfileActivity.class);
                profileIntent.putExtra("user_id", uid);
                context.startActivity(profileIntent);
            }
        });
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Friends").child(uid).removeEventListener(contentEventListener);
    }

    private ValueEventListener contentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String friend_id = snapshot.getKey();

                    final Friend friend = snapshot.getValue(Friend.class);

                    Firebase.getInstance().getDatabaseReference().child("Accounts").child(friend_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            friendList.clear();
                            if(dataSnapshot.exists()){
                                final Account account = dataSnapshot.getValue(Account.class);
                                account.setUid(friend_id);
                                friend.setAccount(account);

                                dataSnapshot.child("online").getRef().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            account.setOnline((boolean) dataSnapshot.getValue());

                                            if(account.isOnline()){
                                                friendList.add(friend);
                                            }
                                            else{
                                                friendList.remove(friend);
                                            }

                                            if(friendList.size() > 0){
                                                onlineLayout.setVisibility(View.VISIBLE);
                                            }
                                            else {
                                                onlineLayout.setVisibility(View.GONE);
                                            }

                                            friendAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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

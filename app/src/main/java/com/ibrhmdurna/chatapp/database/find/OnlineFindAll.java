package com.ibrhmdurna.chatapp.database.find;

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
import java.util.List;
import java.util.Objects;

public class OnlineFindAll implements IFind {

    private Fragment context;

    private List<Friend> friendList;
    private FriendAdapter friendAdapter;
    private RecyclerView friendView;
    private LinearLayout onlineLayout;

    private String uid;

    public OnlineFindAll(Fragment context) {
        this.context = context;
    }

    @Override
    public void buildView() {
        friendView = Objects.requireNonNull(context.getView()).findViewById(R.id.online_container);
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

                    Firebase.getInstance().getDatabaseReference().child("Accounts").child(Objects.requireNonNull(friend_id)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            friendList.clear();
                            if(dataSnapshot.exists()){
                                final Account account = dataSnapshot.getValue(Account.class);

                                if(account != null && friend != null){
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
}
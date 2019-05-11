package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Recent;
import com.ibrhmdurna.chatapp.util.adapter.RecentAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentFindAll implements IFind {

    private Activity context;

    private List<Recent> recentList;
    private List<String> recentIds;
    private RecentAdapter recentAdapter;
    private RecyclerView recentView;

    private NestedScrollView noRecentLayout, recentLayout;

    private String uid;

    public RecentFindAll(Activity context){
        this.context = context;
    }

    @Override
    public void buildView() {
        noRecentLayout = context.findViewById(R.id.no_recent_layout);
        recentLayout = context.findViewById(R.id.recent_layout);
        recentView = context.findViewById(R.id.recent_container);
    }

    @Override
    public void getContent() {
        recentList = new ArrayList<>();
        recentIds = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recentAdapter = new RecentAdapter(context, recentList);
        recentView.setLayoutManager(layoutManager);
        recentView.setItemAnimator(null);
        recentView.setAdapter(recentAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).orderByChild("time").addChildEventListener(contentEventListener);

        getMore();
    }

    @Override
    public void getMore() {
        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).addValueEventListener(moreEventListener);
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).orderByChild("time").removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Recent").child(uid).removeEventListener(moreEventListener);
    }

    private ChildEventListener contentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists()){
                final Recent recent = dataSnapshot.getValue(Recent.class);

                Firebase.getInstance().getDatabaseReference().child("Accounts").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Account account = dataSnapshot.getValue(Account.class);
                            account.setUid(dataSnapshot.getKey());
                            recent.setAccount(account);
                            recentList.add(0, recent);
                            recentIds.add(0, dataSnapshot.getKey());

                            recentAdapter.notifyItemInserted(0);
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
            final Recent recent = dataSnapshot.getValue(Recent.class);
            final int index = recentIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                Firebase.getInstance().getDatabaseReference().child("Accounts").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Account account = dataSnapshot.getValue(Account.class);
                            account.setUid(dataSnapshot.getKey());
                            recent.setAccount(account);
                            recentList.set(index, recent);
                            recentAdapter.notifyItemChanged(index);
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
            int index = recentIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                recentIds.remove(index);
                recentList.remove(index);
                recentAdapter.notifyItemRemoved(index);
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener moreEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                noRecentLayout.setVisibility(View.GONE);
                recentLayout.setVisibility(View.VISIBLE);
            }
            else {
                noRecentLayout.setVisibility(View.VISIBLE);
                recentLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}

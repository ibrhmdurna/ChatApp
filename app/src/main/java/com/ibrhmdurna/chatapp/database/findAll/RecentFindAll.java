package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Recent;
import com.ibrhmdurna.chatapp.util.adapter.RecentAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecentFindAll implements IFind {

    private Activity context;

    private List<Recent> recentList;
    private RecentAdapter recentAdapter;
    private RecyclerView recentView;

    private NestedScrollView noRecentLayout, recentLayout;

    public RecentFindAll(Activity context, RecyclerView recentView, NestedScrollView noRecentLayout, NestedScrollView recentLayout){
        this.context = context;
        this.recentView = recentView;
        this.noRecentLayout = noRecentLayout;
        this.recentLayout = recentLayout;
    }

    @Override
    public void getInformation() {
        recentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recentAdapter = new RecentAdapter(context, recentList);
        recentView.setLayoutManager(layoutManager);
        recentView.setAdapter(recentAdapter);

        String uid = FirebaseAuth.getInstance().getUid();

        noRecentLayout.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    final String recent_uid = dataSnapshot.getKey();

                    final Recent recent = new Recent();
                    recent.setTime((Long) dataSnapshot.child("time").getValue());

                    FirebaseDatabase.getInstance().getReference().child("Accounts").child(recent_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Account account = dataSnapshot.getValue(Account.class);
                                account.setUid(recent_uid);
                                recent.setAccount(account);
                                recentList.add(recent);

                                recentAdapter.notifyDataSetChanged();

                                noRecentLayout.setVisibility(View.GONE);
                                recentLayout.setVisibility(View.VISIBLE);
                            }
                            else {
                                Toast.makeText(context, "Couldn't refresh feed.", Toast.LENGTH_SHORT).show();
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
                    recentList.remove(position);
                    recentAdapter.notifyItemRemoved(position);
                }

                if(recentList.size() <= 0){
                    noRecentLayout.setVisibility(View.VISIBLE);
                    recentLayout.setVisibility(View.GONE);
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
        for(int i = 0; i < recentList.size(); i++){
            if(recentList.get(i).getTime() == time){
                return i;
            }
        }
        return -1;
    }


}

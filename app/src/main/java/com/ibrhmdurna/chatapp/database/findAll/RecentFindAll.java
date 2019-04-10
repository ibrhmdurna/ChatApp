package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
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
import java.util.Collections;
import java.util.Comparator;
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

        FirebaseDatabase.getInstance().getReference().child("Recent").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recentList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String recent_uid = snapshot.getKey();

                        final Recent recent = snapshot.getValue(Recent.class);

                        FirebaseDatabase.getInstance().getReference().child("Accounts").child(recent_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Account account = dataSnapshot.getValue(Account.class);
                                    account.setUid(dataSnapshot.getKey());
                                    recent.setAccount(account);
                                    recentList.add(recent);

                                    if(recentList.size() > 0){
                                        noRecentLayout.setVisibility(View.GONE);
                                        recentLayout.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        noRecentLayout.setVisibility(View.VISIBLE);
                                        recentLayout.setVisibility(View.GONE);
                                    }

                                    shortArrayList();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    noRecentLayout.setVisibility(View.VISIBLE);
                    recentLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void shortArrayList(){
        Collections.sort(recentList, new Comparator<Recent>() {
            @Override
            public int compare(Recent o1, Recent o2) {
                return Long.compare(o2.getTime(),o1.getTime());
            }
        });

        recentAdapter.notifyDataSetChanged();
    }
}

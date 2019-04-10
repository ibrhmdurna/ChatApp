package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Request;
import com.ibrhmdurna.chatapp.util.adapter.RequestAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RequestFindAll implements IFind {

    private Activity context;

    private List<Request> requestList;
    private RequestAdapter requestAdapter;
    private RecyclerView requestView;
    private TextView notFoundView;
    private BottomNavigationView bottomNavigationView;

    public RequestFindAll(Activity context, RecyclerView requestView, TextView notFoundView, BottomNavigationView bottomNavigationView) {
        this.context = context;
        this.requestView = requestView;
        this.notFoundView = notFoundView;
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public void getInformation() {
        requestList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        requestAdapter = new RequestAdapter(context, requestList);
        requestView.setLayoutManager(layoutManager);
        requestView.setAdapter(requestAdapter);

        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Request").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestList.clear();
                if(dataSnapshot.exists()){
                    if(bottomNavigationView.getSelectedItemId() == R.id.requests_item){
                        requestView.setVisibility(View.VISIBLE);
                        notFoundView.setVisibility(View.GONE);
                    }

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String request_id = snapshot.getKey();

                        final Request request = snapshot.getValue(Request.class);
                        Account account = new Account();
                        account.setUid(request_id);
                        request.setAccount(account);
                        requestList.add(request);
                    }

                    sortArrayList();

                }
                else {
                    if(bottomNavigationView.getSelectedItemId() == R.id.requests_item){
                        requestView.setVisibility(View.GONE);
                        notFoundView.setVisibility(View.VISIBLE);
                        notFoundView.setText("No Request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sortArrayList(){
        Collections.sort(requestList, new Comparator<Request>() {
            @Override
            public int compare(Request o1, Request o2) {
                return Long.compare(o2.getTime(), o1.getTime());
            }
        });

        requestAdapter.notifyDataSetChanged();
    }
}

package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public RequestFindAll(Activity context, RecyclerView requestView, TextView notFoundView) {
        this.context = context;
        this.requestView = requestView;
        this.notFoundView = notFoundView;
    }

    @Override
    public void getInformation() {
        requestList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        requestAdapter = new RequestAdapter(context, requestList);
        requestView.setLayoutManager(layoutManager);
        requestView.setAdapter(requestAdapter);

        String uid = FirebaseAuth.getInstance().getUid();

        requestView.setVisibility(View.GONE);
        notFoundView.setVisibility(View.VISIBLE);
        notFoundView.setText("No Request");

        FirebaseDatabase.getInstance().getReference().child("Request").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    requestView.setVisibility(View.VISIBLE);
                    notFoundView.setVisibility(View.GONE);
                    final String request_id = dataSnapshot.getKey();

                    final Request request = dataSnapshot.getValue(Request.class);
                    Account account = new Account();
                    account.setUid(request_id);
                    request.setAccount(account);
                    requestList.add(request);

                    sortArrayList();
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
                    requestList.remove(position);
                    requestAdapter.notifyItemRemoved(position);
                }

                if(requestList.size() <= 0){
                    requestView.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                    notFoundView.setText("No Request");
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
        for(int i = 0; i < requestList.size(); i++){
            if(requestList.get(i).getTime() == time){
                return i;
            }
        }
        return -1;
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

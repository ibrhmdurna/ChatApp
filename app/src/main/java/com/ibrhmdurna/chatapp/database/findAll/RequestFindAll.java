package com.ibrhmdurna.chatapp.database.findAll;

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
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Request;
import com.ibrhmdurna.chatapp.util.adapter.RequestAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RequestFindAll implements IFind {

    private View context;

    private List<Request> requestList;
    private List<String> requestIds;
    private RequestAdapter requestAdapter;
    private RecyclerView requestView;
    private TextView notFoundView;
    private BottomNavigationView bottomNavigationView;

    private String uid;

    public RequestFindAll(View context) {
        this.context = context;
    }

    @Override
    public void buildView() {
        requestView = context.findViewById(R.id.requestsContainer);
        notFoundView = context.getRootView().findViewById(R.id.request_not_found_view);
        bottomNavigationView = context.getRootView().findViewById(R.id.mainBottomNavigationView);
    }

    @Override
    public void getContent() {
        requestList = new ArrayList<>();
        requestIds = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        requestAdapter = new RequestAdapter(context.getContext(), requestList);
        requestView.setLayoutManager(layoutManager);
        requestView.setAdapter(requestAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).orderByChild("time").addChildEventListener(contentEventListener);

        getMore();
    }

    @Override
    public void getMore() {
        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).addValueEventListener(moreEventListener);
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).orderByChild("time").removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Request").child(uid).removeEventListener(moreEventListener);
    }

    private ValueEventListener moreEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                if(bottomNavigationView.getSelectedItemId() == R.id.requests_item){
                    requestView.setVisibility(View.VISIBLE);
                    notFoundView.setVisibility(View.GONE);
                }
            }
            else{
                if(bottomNavigationView.getSelectedItemId() == R.id.requests_item){
                    requestView.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                    notFoundView.setText(context.getContext().getString(R.string.no_request));
                }
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
                Request request = dataSnapshot.getValue(Request.class);
                Account account = new Account();
                account.setUid(dataSnapshot.getKey());
                request.setAccount(account);
                requestList.add(0, request);
                requestIds.add(0, dataSnapshot.getKey());

                requestAdapter.notifyItemInserted(0);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Request request = dataSnapshot.getValue(Request.class);
            int index = requestIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                Account account = new Account();
                account.setUid(dataSnapshot.getKey());
                request.setAccount(account);
                requestList.set(index, request);
                requestAdapter.notifyItemChanged(index);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = requestIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                requestIds.remove(index);
                requestList.remove(index);
                requestAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}

package com.ibrhmdurna.chatapp.database.findAll;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.util.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ChatFindAll implements IFind {

    private Fragment context;

    private List<Chat> chatList;
    private ChatAdapter chatAdapter;
    private RecyclerView chatView;
    private TextView notFoundView;
    private BottomNavigationView bottomNavigationView;

    private String uid;

    public ChatFindAll(Fragment context){
        this.context = context;
        buildView();
    }

    @Override
    public void buildView() {
        chatView = context.getView().findViewById(R.id.messagesContainer);
        notFoundView = context.getView().getRootView().findViewById(R.id.request_not_found_view);
        bottomNavigationView = context.getView().getRootView().findViewById(R.id.mainBottomNavigationView);
    }

    @Override
    public void getContent() {
        chatList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        chatAdapter = new ChatAdapter(context, chatList);
        chatView.setLayoutManager(layoutManager);
        chatView.setAdapter(chatAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).addValueEventListener(contentEventListener);
    }

    @Override
    public void getMore() {

    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).removeEventListener(contentEventListener);
    }

    private ValueEventListener contentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            chatList.clear();
            if(dataSnapshot.exists()){
                if(bottomNavigationView.getSelectedItemId() == R.id.messages_item){
                    chatView.setVisibility(View.VISIBLE);
                    notFoundView.setVisibility(View.GONE);
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    chat.setChatUid(snapshot.getKey());

                    chatList.add(chat);
                }

                shortArrayList();
            }
            else {
                if(bottomNavigationView.getSelectedItemId() == R.id.messages_item){
                    chatView.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                    notFoundView.setText("No Chat");
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void shortArrayList(){
        Collections.sort(chatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return Long.compare(o2.getTime(), o1.getTime());
            }
        });

        chatAdapter.notifyDataSetChanged();
    }
}

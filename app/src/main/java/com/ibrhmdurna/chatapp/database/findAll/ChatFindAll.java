package com.ibrhmdurna.chatapp.database.findAll;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ibrhmdurna.chatapp.models.Chat;
import com.ibrhmdurna.chatapp.util.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChatFindAll implements IFind {

    private Fragment context;

    private List<String> chatIds;
    private List<Chat> chatList;
    private ChatAdapter chatAdapter;
    private RecyclerView chatView;
    private TextView notFoundView;
    private BottomNavigationView bottomNavigationView;

    private String uid;

    public ChatFindAll(Fragment context){
        this.context = context;
    }

    @Override
    public void buildView() {
        chatView = Objects.requireNonNull(context.getView()).findViewById(R.id.messagesContainer);
        notFoundView = context.getView().getRootView().findViewById(R.id.request_not_found_view);
        bottomNavigationView = context.getView().getRootView().findViewById(R.id.mainBottomNavigationView);
    }

    @Override
    public void getContent() {
        chatList = new ArrayList<>();
        chatIds = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        chatAdapter = new ChatAdapter(context, chatList);
        chatView.setLayoutManager(layoutManager);
        chatView.setItemAnimator(null);
        chatView.setAdapter(chatAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).orderByChild("time").addChildEventListener(contentEventListener);

        getMore();
    }

    @Override
    public void getMore() {
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).addValueEventListener(moreEventListener);
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).orderByChild("time").removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Chats").child(uid).removeEventListener(moreEventListener);
    }

    private ChildEventListener contentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists()){
                Chat chat = dataSnapshot.getValue(Chat.class);
                if(chat != null){
                    chat.setChatUid(dataSnapshot.getKey());

                    chatIds.add(0, chat.getChatUid());
                    chatList.add(0, chat);

                    chatAdapter.notifyItemInserted(0);
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Chat chat = dataSnapshot.getValue(Chat.class);

            if(chat != null){
                chat.setChatUid(dataSnapshot.getKey());

                int index = chatIds.indexOf(chat.getChatUid());
                if(index > -1){
                    if(chat.getTime().equals(chatList.get(index).getTime())){
                        chatList.set(index, chat);
                        chatAdapter.notifyItemChanged(index);
                    }
                    else{
                        chatList.remove(index);
                        chatIds.remove(index);
                        chatAdapter.notifyItemRemoved(index);
                        chatList.add(0, chat);
                        chatIds.add(0, chat.getChatUid());
                        chatAdapter.notifyItemInserted(0);
                    }
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = chatIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                chatIds.remove(index);
                chatList.remove(index);

                chatAdapter.notifyItemRemoved(index);
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
                if(bottomNavigationView.getSelectedItemId() == R.id.messages_item){
                    chatView.setVisibility(View.VISIBLE);
                    notFoundView.setVisibility(View.GONE);
                }
            }
            else{
                if(bottomNavigationView.getSelectedItemId() == R.id.messages_item){
                    chatView.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                    notFoundView.setText(context.getString(R.string.no_chat));
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
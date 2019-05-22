package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.database.find.BeginChatFindInfo;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MessageFindAll implements IFind {

    private Activity context;

    private List<Message> messageList;
    private List<String> messageIds;
    private MessageAdapter messageAdapter;
    private RecyclerView messageView;
    private LinearLayoutManager layoutManager;
    private PullRefreshLayout swipeRefreshLayout;

    private String chatUid;

    private static final int TOTAL_LOAD_MESSAGE_COUNT = 30;
    private int CURRENT_POSITION = 0;
    private int CURRENT_MORE_POSITION = 0;

    private String MESSAGE_LAST_KEY;
    private String MESSAGE_PREVIEW_KEY;

    private String uid;

    private Query contentQuery;
    private Query moreQuery;

    public static boolean isRemoved = false;

    private LinearLayout beginLayout;
    private AbstractFind find;

    public MessageFindAll(Activity context, String chatUid){
        this.context = context;
        this.chatUid = chatUid;
        uid = FirebaseAuth.getInstance().getUid();
        ButterKnife.bind(context);
    }

    @Override
    public void buildView() {
        messageView = context.findViewById(R.id.chat_container);
        swipeRefreshLayout = context.findViewById(R.id.chat_swipe_container);
        beginLayout = context.findViewById(R.id.chat_begin_layout);
    }

    @Override
    public void getContent() {
        messageList = new ArrayList<>();
        messageIds = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        messageAdapter = new MessageAdapter(context, messageList, chatUid);
        messageView.setHasFixedSize(true);
        messageView.setItemViewCacheSize(20);
        messageView.setDrawingCacheEnabled(true);
        messageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        messageView.setLayoutManager(layoutManager);
        messageView.setAdapter(messageAdapter);

        contentQuery = Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).limitToLast(TOTAL_LOAD_MESSAGE_COUNT);

        contentQuery.addChildEventListener(contentEventListener);

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() > messageList.size()){
                            CURRENT_MORE_POSITION = 0;
                            getMore();
                        }
                        else{
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).addValueEventListener(beginEventListener);
    }

    @Override
    public void getMore() {
        uid = FirebaseAuth.getInstance().getUid();

        moreQuery = Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).orderByKey().endAt(MESSAGE_LAST_KEY).limitToLast(TOTAL_LOAD_MESSAGE_COUNT);

        moreQuery.addChildEventListener(moreEventListener);
    }

    @Override
    public void onDestroy() {
        if(contentQuery != null){
            contentQuery.removeEventListener(contentEventListener);
        }
        if(moreQuery != null){
            moreQuery.removeEventListener(moreEventListener);
        }
        if(find != null){
            find.onDestroy();
        }
    }

    private ChildEventListener contentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);

            if(message != null){
                message.setMessage_id(dataSnapshot.getKey());

                if(CURRENT_POSITION == 0){
                    MESSAGE_LAST_KEY = dataSnapshot.getKey();
                    MESSAGE_PREVIEW_KEY = dataSnapshot.getKey();
                }

                CURRENT_POSITION++;

                if(isRemoved){

                    messageList.add(0, message);
                    messageIds.add(0, dataSnapshot.getKey());
                    messageAdapter.notifyDataSetChanged();

                    MESSAGE_LAST_KEY = dataSnapshot.getKey();
                    MESSAGE_PREVIEW_KEY = dataSnapshot.getKey();

                    isRemoved = false;
                }
                else{
                    messageList.add(message);
                    messageIds.add(dataSnapshot.getKey());
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    messageView.smoothScrollToPosition(messageList.size() - 1);
                }

                if(dataSnapshot.getChildrenCount() > TOTAL_LOAD_MESSAGE_COUNT){
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);

            if(message != null){
                message.setMessage_id(dataSnapshot.getKey());
                int index = messageIds.indexOf(dataSnapshot.getKey());
                if(index > -1){
                    messageList.remove(index);
                    messageIds.remove(index);
                    messageList.add(index, message);
                    messageIds.add(index, dataSnapshot.getKey());
                    messageAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = messageIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                messageList.remove(index);
                messageIds.remove(index);
                messageAdapter.notifyItemRemoved(index);
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ChildEventListener moreEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);

            if(message != null){
                message.setMessage_id(dataSnapshot.getKey());

                if(!MESSAGE_PREVIEW_KEY.equals(dataSnapshot.getKey())){
                    if(!isRemoved){
                        messageList.add(CURRENT_MORE_POSITION++, message);
                        messageIds.add(CURRENT_MORE_POSITION - 1, dataSnapshot.getKey());
                        messageAdapter.notifyDataSetChanged();
                        layoutManager.scrollToPositionWithOffset(CURRENT_MORE_POSITION - 1, 0);
                    }
                    else{
                        messageAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    MESSAGE_PREVIEW_KEY = MESSAGE_LAST_KEY;
                }

                isRemoved = false;

                if(CURRENT_MORE_POSITION == 1){
                    MESSAGE_LAST_KEY = dataSnapshot.getKey();
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);

            if(message != null){
                message.setMessage_id(dataSnapshot.getKey());
                int index = messageIds.indexOf(dataSnapshot.getKey());
                if(index > -1){
                    messageList.remove(index);
                    messageIds.remove(index);
                    messageList.add(index, message);
                    messageIds.add(index, dataSnapshot.getKey());
                    messageAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = messageIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                messageList.remove(index);
                messageIds.remove(index);
                messageAdapter.notifyItemRemoved(index);
                CURRENT_MORE_POSITION = 0;
            }

            //MESSAGE_LAST_KEY = messageList.get(0).getMessage_id();
            //MESSAGE_PREVIEW_KEY = messageList.get(0).getMessage_id();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener beginEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                beginLayout.setVisibility(View.GONE);
                beginLayout.setEnabled(false);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(true);
            }
            else{
                beginLayout.setVisibility(View.VISIBLE);
                beginLayout.setEnabled(true);
                swipeRefreshLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(false);

                find = new Find(new BeginChatFindInfo(context, chatUid));
                find.getContent();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
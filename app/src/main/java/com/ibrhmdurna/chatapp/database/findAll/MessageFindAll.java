package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Message;
import com.ibrhmdurna.chatapp.util.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MessageFindAll implements IFind {

    private Activity context;

    private List<Message> messageList;
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

    private boolean isRemoved = false;

    public MessageFindAll(Activity context, String chatUid){
        this.context = context;
        this.chatUid = chatUid;
    }

    @Override
    public void buildView() {
        messageView = context.findViewById(R.id.chat_container);
        swipeRefreshLayout = context.findViewById(R.id.chat_swipe_container);
    }

    @Override
    public void getContent() {
        messageList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        messageAdapter = new MessageAdapter(context, messageList, chatUid);
        messageView.setHasFixedSize(true);
        messageView.setItemViewCacheSize(20);
        messageView.setDrawingCacheEnabled(true);
        messageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        messageView.setLayoutManager(layoutManager);
        messageView.setAdapter(messageAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        contentQuery = Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).limitToLast(TOTAL_LOAD_MESSAGE_COUNT);

        contentQuery.addChildEventListener(contentEventListener);

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).addValueEventListener(new ValueEventListener() {
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
    }

    @Override
    public void getMore() {
        uid = FirebaseAuth.getInstance().getUid();

        moreQuery = Firebase.getInstance().getDatabaseReference().child("Messages").child(uid).child(chatUid).orderByKey().endAt(MESSAGE_LAST_KEY).limitToLast(TOTAL_LOAD_MESSAGE_COUNT);

        moreQuery.addChildEventListener(moreEventListener);
    }

    @Override
    public void onDestroy() {
        contentQuery.removeEventListener(contentEventListener);
        if(moreQuery != null){
            moreQuery.removeEventListener(moreEventListener);
        }
    }

    private ChildEventListener contentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            message.setMessage_id(dataSnapshot.getKey());

            if(CURRENT_POSITION == 0){
                MESSAGE_LAST_KEY = dataSnapshot.getKey();
                MESSAGE_PREVIEW_KEY = dataSnapshot.getKey();
            }

            CURRENT_POSITION++;

            if(isRemoved){
                messageList.add(0, message);
                messageAdapter.notifyDataSetChanged();
                isRemoved = false;
            }
            else{
                messageList.add(message);
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                messageView.smoothScrollToPosition(messageList.size() - 1);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            message.setMessage_id(dataSnapshot.getKey());
            int position = findPosition(message.getTime());
            messageList.remove(position);
            messageList.add(position, message);
            messageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Message message = dataSnapshot.getValue(Message.class);
            message.setMessage_id(dataSnapshot.getKey());
            int position = findPosition(message.getTime());
            messageList.remove(position);
            isRemoved = true;
            messageAdapter.notifyItemRemoved(position);
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
            message.setMessage_id(dataSnapshot.getKey());

            if(!MESSAGE_PREVIEW_KEY.equals(dataSnapshot.getKey())){
                if(isRemoved){
                    messageList.add(0, message);
                    messageAdapter.notifyDataSetChanged();
                    isRemoved = false;
                }
                else{
                    messageList.add(CURRENT_MORE_POSITION++, message);
                    messageAdapter.notifyItemInserted(CURRENT_MORE_POSITION);
                    layoutManager.scrollToPositionWithOffset(CURRENT_MORE_POSITION - 1, 0);
                }
            }
            else{
                MESSAGE_PREVIEW_KEY = MESSAGE_LAST_KEY;
            }

            if(CURRENT_MORE_POSITION == 1){
                MESSAGE_LAST_KEY = dataSnapshot.getKey();
            }

            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            message.setMessage_id(dataSnapshot.getKey());
            int position = findPosition(message.getTime());
            messageList.remove(position);
            messageList.add(position, message);
            messageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Message message = dataSnapshot.getValue(Message.class);
            message.setMessage_id(dataSnapshot.getKey());
            int position = findPosition(message.getTime());
            messageList.remove(position);
            isRemoved = true;
            messageAdapter.notifyItemRemoved(position);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private int findPosition(long time){
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getTime().equals(time)){
                return i;
            }
        }

        return messageList.size() - 1;
    }
}

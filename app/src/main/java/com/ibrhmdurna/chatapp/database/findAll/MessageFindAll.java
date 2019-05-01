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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
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

    private String chatUid;

    /*
    private static int PAGE_COUNT = 50;
    private static int PAGE = 1;

    private int TOTAL_MESSAGE;

    private String LAST_MESSAGE;
    */

    private static int TOTAL_LOAD_MESSAGE_COUNT = 50;
    private static int CURRENT_POSITION = 0;
    private static int CURRENT_MORE_POSITION = 0;

    private static String MESSAGE_LAST_KEY;
    private static String MESSAGE_PREVIEW_KEY;

    private String uid;

    public MessageFindAll(Activity context, String chatUid){
        this.context = context;
        this.chatUid = chatUid;
        buildView();
    }

    @Override
    public void buildView() {
        messageView = context.findViewById(R.id.chat_container);
    }

    @Override
    public void getContent() {
        messageList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        messageAdapter = new MessageAdapter(context, messageList, chatUid);
        messageView.setHasFixedSize(true);
        messageView.setItemViewCacheSize(100);
        messageView.setDrawingCacheEnabled(true);
        messageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        messageView.setLayoutManager(layoutManager);
        messageView.setAdapter(messageAdapter);

        uid = FirebaseAuth.getInstance().getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        databaseReference.child("Messages").child(uid).child(chatUid).limitToLast(TOTAL_LOAD_MESSAGE_COUNT).addChildEventListener(childEventListener);
    }

    @Override
    public void getMore(){
        uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        databaseReference.child("Messages").child(uid).child(chatUid).orderByKey().endAt(MESSAGE_LAST_KEY).limitToLast(TOTAL_LOAD_MESSAGE_COUNT).addChildEventListener(moreChildEventListener);
    }

    @Override
    public void onDestroy() {
        FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(chatUid).limitToLast(TOTAL_LOAD_MESSAGE_COUNT).removeEventListener(childEventListener);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(chatUid).orderByKey().endAt(MESSAGE_LAST_KEY).limitToLast(TOTAL_LOAD_MESSAGE_COUNT).removeEventListener(moreChildEventListener);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            message.setMessage_id(dataSnapshot.getKey());
            messageList.add(message);

            if(CURRENT_POSITION == 0){
                MESSAGE_LAST_KEY = dataSnapshot.getKey();
                MESSAGE_PREVIEW_KEY = dataSnapshot.getKey();
            }

            CURRENT_POSITION++;

            if(messageList.size() > 1){
                Message oldMessage = messageList.get(messageList.size() - 2);
                if(message.getFrom().equals(oldMessage.getFrom())){
                    messageList.get(messageList.size() - 2).setProfileVisibility(false);
                    //messageAdapter.notifyItemChanged(messageList.size() - 2);
                }
            }

            messageAdapter.notifyItemInserted(messageList.size() - 1);
            messageView.smoothScrollToPosition(messageList.size() - 1);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            int position = findPosition(message.getTime());
            messageList.remove(position);
            messageList.add(position, message);
            messageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int position = findPosition((long)dataSnapshot.child("time").getValue());
            if(position != -1){
                messageList.remove(position);
                messageAdapter.notifyItemRemoved(position);
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ChildEventListener moreChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);

            if(!MESSAGE_PREVIEW_KEY.equals(dataSnapshot.getKey())){
                messageList.add(CURRENT_MORE_POSITION++, message);
                messageAdapter.notifyItemInserted(CURRENT_MORE_POSITION);
                layoutManager.scrollToPositionWithOffset(CURRENT_MORE_POSITION + 1, 0);
            }
            else{
                MESSAGE_PREVIEW_KEY = MESSAGE_LAST_KEY;
            }

            if(CURRENT_MORE_POSITION == 1){
                MESSAGE_LAST_KEY = dataSnapshot.getKey();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            int position = findPosition(message.getTime());
            if(message.isSend() && message.isReceive()){
                messageList.get(position).setSend(true);
                messageList.get(position).setReceive(true);
                messageAdapter.notifyItemChanged(position);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int position = findPosition((long)dataSnapshot.child("time").getValue());
            if(position != -1){
                messageList.remove(position);
                messageAdapter.notifyItemRemoved(position);
            }
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

    /*
    private ValueEventListener contentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            messageList.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    message.setMessage_id(snapshot.getKey());
                    messageList.add(message);
                }

                TOTAL_MESSAGE = messageList.size();

                if(LAST_MESSAGE == null){
                    LAST_MESSAGE = messageList.get(messageList.size() - 1).getMessage_id();
                }

                if(!LAST_MESSAGE.equals(messageList.get(messageList.size() - 1).getMessage_id())){
                    LAST_MESSAGE = messageList.get(messageList.size() - 1).getMessage_id();
                    messageView.smoothScrollToPosition(messageList.size() - 1);
                }

                messageAdapter.notifyDataSetChanged();
            }
            else{
                messageAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener moreEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            messageList.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    message.setMessage_id(snapshot.getKey());
                    messageList.add(message);
                }

                if(messageList.size() != TOTAL_MESSAGE){
                    PAGE++;
                    layoutManager.scrollToPositionWithOffset(PAGE_COUNT - 1, 0);
                    messageAdapter.notifyDataSetChanged();
                }

                TOTAL_MESSAGE = messageList.size();
            }
            else{
                messageAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    */
}

package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private static int PAGE_COUNT = 30;
    private static int PAGE = 1;

    private int TOTAL_MESSAGE;

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

        String uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        databaseReference.child("Messages").child(uid).child(chatUid).limitToLast(PAGE * PAGE_COUNT).addValueEventListener(new ValueEventListener() {
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

                    messageAdapter.notifyDataSetChanged();
                    messageView.smoothScrollToPosition(messageList.size() - 1);
            }
                else{
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getMore(){
        String uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        databaseReference.child("Messages").child(uid).child(chatUid).limitToLast((PAGE + 1) * PAGE_COUNT).addListenerForSingleValueEvent(new ValueEventListener() {
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
        });
    }
}

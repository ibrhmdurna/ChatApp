package com.ibrhmdurna.chatapp.database.findAll;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.database.bridge.IFind;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Block;
import com.ibrhmdurna.chatapp.util.adapter.BlockAdapter;

import java.util.ArrayList;
import java.util.List;

public class BlockFindAll implements IFind {

    private Activity context;

    private List<Block> blockList;
    private List<String> blockIds;
    private BlockAdapter blockAdapter;
    private RecyclerView blockView;
    private NestedScrollView notFoundView;

    private String uid;

    public BlockFindAll(Activity context) {
        this.context = context;
        uid = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public void buildView() {
        blockView = context.findViewById(R.id.blocks_container);
        notFoundView = context.findViewById(R.id.no_blocked_account_view);
    }

    @Override
    public void getContent() {
        blockList = new ArrayList<>();
        blockIds = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        blockAdapter = new BlockAdapter(context, blockList);
        blockView.setLayoutManager(linearLayoutManager);
        blockView.setItemAnimator(null);
        blockView.setAdapter(blockAdapter);

        Firebase.getInstance().getDatabaseReference().child("Blocks").child(uid).addChildEventListener(contentEventListener);

        getMore();
    }

    @Override
    public void getMore() {
        Firebase.getInstance().getDatabaseReference().child("Blocks").child(uid).addValueEventListener(moreEventListener);
    }

    @Override
    public void onDestroy() {
        Firebase.getInstance().getDatabaseReference().child("Blocks").child(uid).removeEventListener(contentEventListener);
        Firebase.getInstance().getDatabaseReference().child("Blocks").child(uid).removeEventListener(moreEventListener);
    }

    private ValueEventListener moreEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                blockView.setVisibility(View.VISIBLE);
                notFoundView.setVisibility(View.GONE);
            }
            else{
                blockView.setVisibility(View.GONE);
                notFoundView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ChildEventListener contentEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Block block = dataSnapshot.getValue(Block.class);
            if(block != null){
                Account account = new Account();
                account.setUid(dataSnapshot.getKey());
                block.setAccount(account);
                blockIds.add(dataSnapshot.getKey());
                blockList.add(block);

                blockAdapter.notifyItemInserted(blockList.size() - 1);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Block block = dataSnapshot.getValue(Block.class);
            int index = blockIds.indexOf(dataSnapshot.getKey());
            if(index > -1 && block != null){
                Account account = new Account();
                account.setUid(dataSnapshot.getKey());
                block.setAccount(account);
                blockList.set(index, block);
                blockAdapter.notifyItemChanged(index);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int index = blockIds.indexOf(dataSnapshot.getKey());
            if(index > -1){
                blockIds.remove(index);
                blockList.remove(index);
                blockAdapter.notifyItemRemoved(index);
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

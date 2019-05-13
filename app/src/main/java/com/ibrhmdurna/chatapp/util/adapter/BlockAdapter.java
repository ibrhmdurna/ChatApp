package com.ibrhmdurna.chatapp.util.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Firebase;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Block;
import com.ibrhmdurna.chatapp.util.controller.DialogController;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {

    private Activity context;
    private List<Block> blockList;

    public BlockAdapter(Activity context, List<Block> blockList) {
        this.context = context;
        this.blockList = blockList;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.block_layout, viewGroup, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder blockViewHolder, int i) {
        Block block = blockList.get(i);
        blockViewHolder.setDate(block, i);
    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView profileText;
        private TextView nameSurname, email;
        private TextView unblockItem;
        private View line;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.block_profile_image);
            profileText = itemView.findViewById(R.id.block_profile_text);
            nameSurname = itemView.findViewById(R.id.block_name_surname);
            email = itemView.findViewById(R.id.block_email);
            unblockItem = itemView.findViewById(R.id.unblock_btn);
            line = itemView.findViewById(R.id.block_line);
        }

        public void setDate(final Block block, int position){
            Firebase.getInstance().getDatabaseReference().child("Accounts").child(block.getAccount().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Account account = dataSnapshot.getValue(Account.class);
                        if(account != null){
                            account.setUid(dataSnapshot.getKey());
                            block.setAccount(account);

                            nameSurname.setText(block.getAccount().getNameSurname());
                            email.setText(block.getAccount().getEmail());
                            setProfileImage(block.getAccount().getThumb_image(), block.getAccount().getName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(position == 0){
                line.setVisibility(View.GONE);
            }
            else{
                line.setVisibility(View.VISIBLE);
            }

            unblockItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = DialogController.getInstance().dialogCustom(context, context.getString(R.string.are_you_want_to_unblock), context.getString(R.string.cancel), context.getString(R.string.unblock));
                    TextView positiveBtn = dialog.findViewById(R.id.dialog_positive_btn);
                    assert positiveBtn != null;
                    positiveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Delete.getInstance().unblock(block.getAccount().getUid());
                        }
                    });
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("user_id", block.getAccount().getUid());
                    context.startActivity(profileIntent);
                }
            });
        }

        private void setProfileImage(final String value, String nameValue){

            if(value.substring(0,8).equals("default_")){
                String text = value.substring(8,9);
                int index = Integer.parseInt(text);
                setProfileImage(index, profileImage);
                String name = nameValue.substring(0,1);
                profileText.setText(name);
                profileText.setVisibility(View.VISIBLE);
            }
            else {
                if(context != null){
                    try {
                        Glide.with(context).load(value).placeholder(R.drawable.default_avatar).into(profileImage);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                profileText.setText(null);
                profileText.setVisibility(View.GONE);
            }

        }

        private void setProfileImage(int index, CircleImageView profileImage) {
            switch (index){
                case 0:
                    profileImage.setImageResource(R.drawable.ic_avatar_0);
                    break;
                case 1:
                    profileImage.setImageResource(R.drawable.ic_avatar_1);
                    break;
                case 2:
                    profileImage.setImageResource(R.drawable.ic_avatar_2);
                    break;
                case 3:
                    profileImage.setImageResource(R.drawable.ic_avatar_3);
                    break;
                case 4:
                    profileImage.setImageResource(R.drawable.ic_avatar_4);
                    break;
                case 5:
                    profileImage.setImageResource(R.drawable.ic_avatar_5);
                    break;
                case 6:
                    profileImage.setImageResource(R.drawable.ic_avatar_6);
                    break;
                case 7:
                    profileImage.setImageResource(R.drawable.ic_avatar_7);
                    break;
                case 8:
                    profileImage.setImageResource(R.drawable.ic_avatar_8);
                    break;
                case 9:
                    profileImage.setImageResource(R.drawable.ic_avatar_9);
                    break;
            }
        }
    }
}

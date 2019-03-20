package com.ibrhmdurna.chatapp.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

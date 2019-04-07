package com.ibrhmdurna.chatapp.util.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.R;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private List<String> list;

    public RecentAdapter(List<String> list){
        this.list = list;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_layout, viewGroup, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder recentViewHolder, int i) {
        recentViewHolder.setNameSurname(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder{

        private TextView nameSurname;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);

            nameSurname = itemView.findViewById(R.id.recent_name_surname);
        }

        public void setNameSurname(String value){
            nameSurname.setText(value);
        }
    }
}

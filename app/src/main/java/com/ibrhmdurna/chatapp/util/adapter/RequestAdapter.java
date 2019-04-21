package com.ibrhmdurna.chatapp.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.models.Request;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<Request> requestList;

    public RequestAdapter(Context context, List<Request> requestList){
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.request_layout, viewGroup, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, final int i) {
        final Request request = requestList.get(i);

        FirebaseDatabase.getInstance().getReference().child("Accounts").child(request.getAccount().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Account account = dataSnapshot.getValue(Account.class);
                    account.setUid(dataSnapshot.getKey());
                    request.setAccount(account);

                    requestViewHolder.setNameSurname(request.getAccount().getNameSurname());
                    requestViewHolder.setEmail(request.getAccount().getEmail());
                    requestViewHolder.setProfileImage(request.getAccount().getThumb_image(), request.getAccount().getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(getItemCount() - 1 == i){
            requestViewHolder.line.setVisibility(View.GONE);
        }
        else {
            requestViewHolder.line.setVisibility(View.VISIBLE);
        }

        requestViewHolder.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete.getInstance().request(request.getAccount().getUid());
            }
        });

        requestViewHolder.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Insert.getInstance().friend(request.getAccount().getUid());
            }
        });

        requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.putExtra("user_id", request.getAccount().getUid());
                context.startActivity(profileIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView profileText;
        private TextView nameSurname, email;

        private TextView cancelView, confirmView;

        private View line;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.request_profile_image);
            profileText = itemView.findViewById(R.id.request_profile_text);
            nameSurname = itemView.findViewById(R.id.request_name_surname);
            email = itemView.findViewById(R.id.request_email);
            line = itemView.findViewById(R.id.request_line);
            cancelView = itemView.findViewById(R.id.req_cancel);
            confirmView = itemView.findViewById(R.id.req_confirm);
        }

        public void setEmail(String value){
            email.setText(value);
        }

        public void setNameSurname(String value){
            nameSurname.setText(value);
        }

        public void setProfileImage(final String value, String nameValue){

            if(value.substring(0,8).equals("default_")){
                String text = value.substring(8,9);
                int index = Integer.parseInt(text);
                setProfileImage(index, profileImage);
                String name = nameValue.substring(0,1);
                profileText.setText(name);
                profileText.setVisibility(View.VISIBLE);
            }
            else {
                final Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(true);
                picasso.load(value).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        picasso.load(value).placeholder(R.drawable.default_avatar).into(profileImage);
                    }
                });
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

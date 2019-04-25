package com.ibrhmdurna.chatapp.util.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Delete;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Recent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private Activity context;
    private List<Recent> recentList;

    public RecentAdapter(Activity context, List<Recent> recentList){
        this.context = context;
        this.recentList = recentList;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_layout, viewGroup, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder recentViewHolder, final int i) {
        final Recent recent = recentList.get(i);

        recentViewHolder.setNameSurname(recent.getAccount().getNameSurname());
        recentViewHolder.setEmail(recent.getAccount().getEmail());
        recentViewHolder.setProfileImage(recent.getAccount().getThumb_image(), recentList.get(i).getAccount().getName());

        final String uid = FirebaseAuth.getInstance().getUid();

        recentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recent.getAccount().getUid().equals(uid)){
                    Intent accountIntent = new Intent(context, MainActivity.class);
                    accountIntent.putExtra("page","Account");
                    context.startActivity(accountIntent);
                }
                else {
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("user_id", recent.getAccount().getUid());
                    context.startActivity(profileIntent);
                }
                Insert.getInstance().recent(recent.getAccount().getUid());
            }
        });

        recentViewHolder.getClearBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete.getInstance().recent(recent.getAccount().getUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    class RecentViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImage;
        private TextView nameSurname, email, profileText;
        private ImageButton clearBtn;

        RecentViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.recent_profile_image);
            nameSurname = itemView.findViewById(R.id.recent_name_surname);
            email = itemView.findViewById(R.id.recent_email);
            profileText = itemView.findViewById(R.id.recent_profile_text);
            clearBtn = itemView.findViewById(R.id.recent_clear_btn);
        }

        private ImageButton getClearBtn() {
            return clearBtn;
        }

        private void setEmail(String value){
            email.setText(value);
        }

        private void setNameSurname(String value){
            nameSurname.setText(value);
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
                final Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
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

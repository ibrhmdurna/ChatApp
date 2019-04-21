package com.ibrhmdurna.chatapp.util.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.database.Insert;
import com.ibrhmdurna.chatapp.local.ProfileActivity;
import com.ibrhmdurna.chatapp.main.MainActivity;
import com.ibrhmdurna.chatapp.models.Account;
import com.ibrhmdurna.chatapp.util.UniversalImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Activity context;
    private List<Account> accountList;

    public SearchAdapter(Activity context, List<Account> accountList){
        this.context = context;
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_layout, viewGroup,false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {

        final Account account = accountList.get(i);

        if(!account.getUid().equals("False")){
            searchViewHolder.getProfileLayout().setVisibility(View.VISIBLE);
            searchViewHolder.getEmail().setVisibility(View.VISIBLE);
            searchViewHolder.setNameSurname(account.getNameSurname());
            searchViewHolder.setEmail(account.getEmail());
            searchViewHolder.setProfileImage(account.getThumb_image(), account.getName());
            searchViewHolder.itemView.setEnabled(true);
        }
        else {
            searchViewHolder.setNameSurname("\""+ account.getName() + "\" cloud not be found.");
            searchViewHolder.getProfileLayout().setVisibility(View.GONE);
            searchViewHolder.getEmail().setVisibility(View.GONE);
            searchViewHolder.itemView.setEnabled(false);
        }

        final String uid = FirebaseAuth.getInstance().getUid();

        searchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account.getUid().equals(uid)){
                    Intent accountIntent = new Intent(context, MainActivity.class);
                    accountIntent.putExtra("page","Account");
                    context.startActivity(accountIntent);
                }
                else {
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("user_id", account.getUid());
                    context.startActivity(profileIntent);
                }
                Insert.getInstance().recent(account.getUid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout profileLayout;
        private CircleImageView profileImage;
        private TextView profileText, nameSurname, email;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            profileLayout = itemView.findViewById(R.id.search_profile_layout);
            profileImage = itemView.findViewById(R.id.search_profile_image);
            profileText = itemView.findViewById(R.id.search_profile_text);
            nameSurname = itemView.findViewById(R.id.search_name_surname);
            email = itemView.findViewById(R.id.search_email);
        }

        public RelativeLayout getProfileLayout() {
            return profileLayout;
        }

        public TextView getEmail() {
            return email;
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

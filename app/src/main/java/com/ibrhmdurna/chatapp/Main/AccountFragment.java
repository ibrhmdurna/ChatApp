package com.ibrhmdurna.chatapp.Main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.Settings.SettingsActivity;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ImageButton settingsButton;
    private CircleImageView profileImage;
    private TextView profileText;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getTheme(getContext());
        view = inflater.inflate(R.layout.fragment_account, container, false);

        toolsManagement();

        randomProfileImage();
        profileText.setText("N");

        return view;
    }

    private void randomProfileImage(){
        Random r = new Random();
        int index = r.nextInt(10);
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

    private void clickProcess(){
        settingsButton.setOnClickListener(this);
    }

    private void buildView(){
        profileImage = view.findViewById(R.id.profile_image);
        profileText = view.findViewById(R.id.profile_image_text);
        settingsButton = view.findViewById(R.id.account_settings_button);
    }

    private void toolsManagement(){
        buildView();
        clickProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.account_settings_button:
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settings);
                break;
        }
    }
}

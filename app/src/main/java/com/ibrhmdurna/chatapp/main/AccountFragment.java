package com.ibrhmdurna.chatapp.main;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.ibrhmdurna.chatapp.application.ViewComponentFactory;
import com.ibrhmdurna.chatapp.application.App;
import com.ibrhmdurna.chatapp.database.bridge.AbstractFind;
import com.ibrhmdurna.chatapp.database.bridge.Find;
import com.ibrhmdurna.chatapp.database.find.AccountFindInfo;
import com.ibrhmdurna.chatapp.databinding.FragmentAccountBinding;
import com.ibrhmdurna.chatapp.local.FriendsActivity;
import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.settings.SettingsActivity;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements ViewComponentFactory, View.OnClickListener {

    private FragmentAccountBinding binding;
    private ImageButton settingsButton;
    private LinearLayout friendView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        App.Theme.getInstance().getTheme(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);

        toolsManagement();

        return binding.getRoot();
    }

    private void getAccountInformation(){
        AbstractFind find = new Find(new AccountFindInfo(binding));
        find.getContent();
    }

    private void clickProcess(){
        settingsButton.setOnClickListener(this);
        friendView.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getAccountInformation();
    }

    @Override
    public void buildView(){
        settingsButton = binding.getRoot().findViewById(R.id.account_settings_button);
        friendView = binding.getRoot().findViewById(R.id.account_friends_button);
    }

    @Override
    public void toolsManagement(){
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
            case R.id.account_friends_button:
                Intent friends = new Intent(getActivity(), FriendsActivity.class);
                friends.putExtra("isAccount", true);
                startActivity(friends);
                break;
        }
    }
}

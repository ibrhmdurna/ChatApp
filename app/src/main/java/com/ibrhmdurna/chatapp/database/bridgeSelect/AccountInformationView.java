package com.ibrhmdurna.chatapp.database.bridgeSelect;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInformationView extends AccountContent {

    public AccountInformationView(Implementor implementor) {
        super(implementor);
    }

    @Override
    public void getAccountInformation() {
        implementor.getAccountInformation();
    }

    @Override
    public void setProfileImage(int index, CircleImageView profileImage) {
        implementor.setProfileImage(index, profileImage);
    }
}

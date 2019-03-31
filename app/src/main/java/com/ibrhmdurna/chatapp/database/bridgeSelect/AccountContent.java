package com.ibrhmdurna.chatapp.database.bridgeSelect;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class AccountContent {

    protected Implementor implementor;

    public AccountContent(Implementor implementor){
        this.implementor = implementor;
    }

    public abstract void getAccountInformation();
    public abstract void setProfileImage(int index, CircleImageView profileImage);
}

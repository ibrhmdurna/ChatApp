package com.ibrhmdurna.chatapp.database.list;

import android.util.Log;

import com.ibrhmdurna.chatapp.database.bridgeList.Implementor;

public class ChatList implements Implementor {

    @Override
    public void viewList() {
        Log.e("------", "Chat List");
    }
}

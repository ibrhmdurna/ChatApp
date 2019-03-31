package com.ibrhmdurna.chatapp.util.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AppController {

    private static AppController instance;

    private AppController(){}

    public static AppController getInstance() {
        if(instance == null){
            synchronized (AppController.class){
                instance = new AppController();
            }
        }
        return instance;
    }

    public void closeKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
